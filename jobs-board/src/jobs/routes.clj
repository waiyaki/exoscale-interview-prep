(ns jobs.routes
  (:require
   [muuntaja.core :as m]
   [reitit.coercion.spec]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.util.http-response :as response]
   [taoensso.timbre :as timbre]
   [jobs.db.core :as db]
   [jobs.handlers :as handlers]
   [jobs.handlers.jobs :as handlers.jobs]
   [jobs.specs :as specs]))


(def exception-middleware
  (exception/create-exception-middleware
    (merge exception/default-handlers
      {::response/response handlers/error
       ::exception/default
       (fn [^Exception e _]
         (timbre/error e ::error)
         {:status 500
          :body {:type "Server Error"
                 :message "A server error occurred."}})})))


(defn wrap-db [handler id db]
  (fn [req]
    (handler (assoc req :jobs/db db))))


(def app
  (ring/ring-handler
    (ring/router
      [["/" {:get {:handler handlers/index}}]
       ["/jobs" {:middleware [[wrap-db ::wrap-db db/db]]}
        ["" {:get  {:handler handlers.jobs/enumerate}
             :post {:parameters {:body ::specs/job}
                    :handler    handlers.jobs/create!}}]
        ["/:id" {:delete {:parameters {:path {:id ::specs/id}}
                          :handler    handlers.jobs/delete!}}]]]
      {:data {:coercion   reitit.coercion.spec/coercion
              :muuntaja   m/instance
              :middleware [;; query-params & form-params. Wraps `ring.middleware.params/wrap-params`
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception-middleware
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodies
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware]}})
    (ring/routes
      (ring/create-default-handler))))
