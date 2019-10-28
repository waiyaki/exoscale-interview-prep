(ns jobs.handlers)


(defn index [req]
  {:status 200
   :body   {:message "Welcome to the jobs board API!"}})


(defn error [^Exception e _]
  (let [data (ex-data e)]
    (:response data)))
