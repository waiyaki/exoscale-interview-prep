(ns jobs.db.companies
  (:require
   [jobs.db.core :as db]
   [ring.util.http-response :as response]))


(defn create! [db {:keys [name] :as opts}]
  (if (some? name)
    (let [id   (java.util.UUID/randomUUID)
          path [:companies id]]
      (get-in (db/update-db! db path {:name name :id id}) path))
    (response/bad-request! {:message "`name` is required to create a company."})))


(defn retrieve [db id]
  (db/retrieve db [:companies id]))


(defn find-by-id [db id]
  (if-let [company (retrieve db id)]
    company
    (response/not-found! {:message "Company not found."})))


(defn find-or-create! [db {:keys [id name]}]
  (if (some? id)
    (find-by-id db id)
    (create! db {:name name})))
