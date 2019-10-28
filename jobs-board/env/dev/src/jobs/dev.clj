(ns jobs.dev
  (:require
   [ring.middleware.reload :refer [wrap-reload]]
   [jobs.core :as jobs]
   [jobs.routes :as routes])
  (:gen-class))


(defn -main [& args]
  (jobs/start-app args {:handler (wrap-reload #'routes/app)}))
