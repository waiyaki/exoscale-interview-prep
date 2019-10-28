(ns jobs.db.jobs
  (:require
   [jobs.db.core :as db]
   [jobs.db.companies :as companies]))


(defn- generate!
  "Generate a map representing a job. Creates the company when it doesn't exist,
  otherwise updates and returns"
  [db {:keys [title description company] :as opts}]
  (let [company (companies/find-or-create! db company)]
    {:id          (java.util.UUID/randomUUID)
     :title       title
     :description description
     :company     company}))


(defn create!
  "Create a job. `company` can be provided as a map with an `:id` key when the
  company exists or with a `:name` key when the company does not exist and should
  be created."
  [db {:keys [title description company] :as opts}]
  (let [job  (generate! db opts)
        path [:jobs (:id job)]]
    (db/update-db! db path (assoc job :company (-> job :company :id)))
    job))


(defn enumerate
  "List all available jobs."
  [db]
  (let [jobs (db/retrieve db [:jobs])]
    (for [[id job] jobs]
      (assoc job :company (companies/retrieve db (:company job))))))


(defn delete!
  "Delete the job with the provided `id` from the db."
  [db id]
  (db/delete! db [:jobs] id))
