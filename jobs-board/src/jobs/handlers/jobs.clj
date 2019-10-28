(ns jobs.handlers.jobs
  (:require
   [jobs.db.jobs :as db.jobs]
   [ring.util.http-response :as response]))


(defn enumerate
  "List all available jobs."
  [{:jobs/keys [db] :as req}]
  (response/ok (db.jobs/enumerate db)))


(defn create!
  "Create a job position."
  [{:jobs/keys [db]
    {{:keys [title description company] :as params} :body} :parameters :as req}]
  (response/created nil (db.jobs/create! db params)))


(defn delete!
  "Delete a job position."
  [{:jobs/keys [db]
    {{:keys [id]} :path} :parameters :as req}]
  (db.jobs/delete! db id)
  (response/no-content))
