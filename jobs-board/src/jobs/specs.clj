(ns jobs.specs
  (:require
   [clojure.spec.alpha :as s]))


(s/def ::id uuid?)
(s/def ::name string?)
(s/def ::title string?)
(s/def ::description string?)

(s/def ::company (s/keys :req-un [(or ::id ::name)]))

(s/def ::job (s/keys :req-un [::title ::description ::company]))
