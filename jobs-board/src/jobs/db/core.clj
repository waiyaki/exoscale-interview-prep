(ns jobs.db.core)


(defonce db (atom nil))


(defn update-db!
  "Update `db` at `db-path` and set `params` as the item at `db-path`."
  [db db-path params]
  (swap! db assoc-in db-path params))


(defn retrieve
  "Retrieve and return the item as `db-path`."
  [db db-path]
  (get-in @db db-path))


(defn delete!
  "Delete (from the `db`) the item under `key` at `db-path`."
  [db db-path key]
  (swap! db update-in db-path dissoc key))
