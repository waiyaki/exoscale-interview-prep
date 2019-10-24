(ns metered.usage
  (:require
   [clj-time.core :as t]
   [clj-time.coerce :as c]))


(defn- usage-record [[uuid [start end]]]
  (let [[create destroy] (if (= :usage.event/create (:usage/event start))
                           [start end]
                           [end start])]
    (when destroy
      {:usage/uuid     uuid
       :usage/resource (:usage/resource create)
       :usage/account  (:usage/account create)
       :usage/duration (t/in-minutes
                         (t/interval
                           (c/from-date (:usage/timestamp create))
                           (c/from-date (:usage/timestamp destroy))))})))


(defn process-usage
  "Create resource usage records from a sequence of usage events, for all usage
  events that have a corresponding timestamp indicating usage completion."
  [events]
  (->> events
    (group-by :usage/uuid)
    (map usage-record)
    (remove nil?)))
