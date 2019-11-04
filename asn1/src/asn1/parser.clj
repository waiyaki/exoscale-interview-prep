(ns asn1.parser
  (:require [clojure.string  :as str]
            [clojure.java.io :as io]
            [clojure.pprint  :refer [pprint]])
  (:import java.io.RandomAccessFile
           java.nio.ByteBuffer
           java.nio.charset.StandardCharsets
           javax.xml.bind.DatatypeConverter
           java.time.LocalDateTime
           java.time.format.DateTimeFormatter)
  (:gen-class))


(defn base64-extract
  [path]
  (reduce str "" (remove #(str/starts-with? % "----") (line-seq (io/reader path)))))


(defn base64-bytes
  [path]
  (let [b64-str ^String (base64-extract path)]
    (DatatypeConverter/parseBase64Binary b64-str)))


(defn base64-buffer
  [path]
  (ByteBuffer/wrap (base64-bytes path)))


(declare parsers parse)


(defn known-type?
  "Return `true` if we have a parser for this ASN `tag`."
  [tag]
  (some? (parsers tag)))


(defn- resolve-tag [tag tag-type]
  (case tag
    0x01 :boolean
    0x02 :integer
    0x03 :bit-string
    0x04 :octet-string
    0x05 :null
    0x06 :object-identifier
    0x0C :utf8-string
    0x13 :printable-string
    0x14 :T61-string
    0x16 :IA5-string
    0x17 :utc-time
    0x30 :sequence
    0x31 :set
    tag-type))


(defn parse-tag
  "Parse a single byte from the buffer into an ASN.1 tag type and the
  corresponding tag name. If the tag type is unknown, the tag representation
  is used as the tag name."
  [b]
  (let [tag      (.get b)
        tag-type (format "0x%X" tag)]
    {:tag-name (resolve-tag tag tag-type)
     :tag-type tag-type}))


(defn- parse-into
  "Parse `length` bytes from buffer `b`, putting the results into `coll`."
  [coll]
  (fn [^ByteBuffer b length]
    (let [barray (byte-array length)
          _      (.get b barray)
          buffer (ByteBuffer/wrap barray)]
      {:children (parse buffer coll)})))


(def parse-sequence (parse-into []))
(def parse-set (parse-into #{}))


(defn parse-boolean [b _]
  (let [v (.get b)]
    {:value (if (= v 1)
              true
              false)}))


(defn parse-integer [b length]
  (let [barray  (byte-array length)
        _       (.get b barray)
        big-int (BigInteger. barray)]
    {:value (cond
              (< length 2) (Integer/parseInt (DatatypeConverter/printHexBinary barray) 16)
              (< length 4) (.intValue big-int)
              (< length 8) (.longValue big-int)
              :else        big-int)}))


(defn parse-null [_ _]
  {:value nil})


(defn parse-bit-string [b length]
  (let [barray (byte-array length)
        _      (.get b barray)]
    {:value (-> barray
              (DatatypeConverter/printHexBinary)
              (BigInteger. 16)
              (.toString 2))}))


(defn parse-object-identifier [b length]
  (let [barray (byte-array length)
        _      (.get b barray)]
    {:value (DatatypeConverter/printHexBinary barray)}))


(defn- parse-value-or-children
  "Attempt to parse `length` bytes from a buffer `b`. If the tag in the first byte is
  known, attempt to parse the children of the that buffer. Otherwise, return the
  hex string representation of the contents of the buffer."
  [b length]
  (let [barray (byte-array length)
        _      (.get b barray)
        buffer (ByteBuffer/wrap barray)
        ptag   (parse-tag buffer)
        _      (.rewind buffer)]
    (if (known-type? (:tag-name ptag))
      {:children (parse buffer [])}
      {:value (DatatypeConverter/printHexBinary barray)})))


(defn parse-octet-string [b length]
  (parse-value-or-children b length))


(defn parse-utf8-string [b length]
  (let [barray (byte-array length)
        _      (.get b barray)]
    {:value (String. barray)}))


(defn parse-utc-time
  "Parse `length` bytes from buffer `b` into an ISO string representation of a LocalDateTime."
  [b length]
  (let [time      (:value (parse-utf8-string b length))
        formatted (.format (LocalDateTime/parse time
                             (DateTimeFormatter/ofPattern "yyMMddHHmm[ss][X]"))
                    DateTimeFormatter/ISO_LOCAL_DATE_TIME)]
    {:value formatted}))


(defn parse-unknown [b length]
  (parse-value-or-children b length))


(defn parse-length
  "Of a tag, parse the length of its contents, and the length of the length when
  then length is more than can be stored in 7 bits."
  [b]
  (let [len        (.get b)
        len-size   (bit-and 0x7F len)
        actual-len (if (= len len-size)
                     len
                     (:value (parse-integer b len-size)))]
    {:length      actual-len
     :length-size (if (= len len-size) 0 len-size)}))


(def parsers {:set               parse-set
              :sequence          parse-sequence
              :boolean           parse-boolean
              :integer           parse-integer
              :null              parse-null
              :octet-string      parse-octet-string
              :bit-string        parse-bit-string
              :utf8-string       parse-utf8-string
              :printable-string  parse-utf8-string
              :T61-string        parse-utf8-string
              :IA5-string        parse-utf8-string
              :utc-time          parse-utc-time
              :object-identifier parse-object-identifier})


(defn parser
  "Given an ASN.1 `tag` name, return the appropriate parser for the corresponding tag.

  Parsers are expected to take in a ByteBuffer `b` and the number of bytes
  to parse from the buffer, `length`.

  If the `tag` is known, a default parser is used."
  [tag]
  (get parsers tag parse-unknown))


(defn parse
  "Given a buffer `b` and a `coll`, parse the contents of `b`, collecting the
  parsing results into `coll`."
  [^ByteBuffer b coll]
  (loop [buffer b
         data   coll]
    (if (.hasRemaining buffer)
      (let [tag     (parse-tag b)
            length  (parse-length b)
            parser* (parser (:tag-name tag))
            parsed  (parser* b (:length length))]
        (recur b (conj data (merge tag length parsed))))
      data)))


(defn parse-asn1
  "Parse the contents of a buffer `bb` into a collection of  ASN.1 tags."
  [bb]
  (parse bb {}))


(defn -main [& args]
  (if-let [key-path (first args)]
    (pprint (parse-asn1 (base64-buffer key-path)))
    (binding [*out* *err*]
      (println "no path given")
      (System/exit 1))))


(comment
  (defn parse [b data]
    (let [tag (atom nil)]
      (loop [buffer b
             data   data
             n      0]
        (if (.hasRemaining buffer)
          (let [ptag    (parse-tag b)
                plength (parse-length b)
                _       (prn (:tag-name ptag) (clojure.string/join (repeat n "--")) " start data:" (count data))
                parser* (parser (:tag-name ptag))
                parsed  (parser* b (:length plength))]
            (prn (:tag-name ptag) (clojure.string/join (repeat n "--")) " parsed:" parsed)
            (swap! tag assoc :tag-name (:tag-name ptag))
            (recur b (conj data (merge ptag plength parsed)) (inc n)))
          (do
            (prn (clojure.string/join (repeat n "--")) (:tag-name @tag) " end data:" (count data))
            data)))))

  )
