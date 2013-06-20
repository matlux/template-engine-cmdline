(ns fleettest.core
  (:use fleet))

(defmacro fileet
  "Convinient way to define test templates"
  [args filename]
  `(fleet ~args (slurp (str "./" ~filename ".fleet")) {:escaping :bypass}))


(def test-posts
  [{:body "First Post"  :tags ["tag1" "tag2" "tag3"]},
   {:body "Second Post" :tags ["tag1" "tag2"]}])

(def test-post
  (first test-posts))


(defn -main
  "I don't do a whole lot."
  [x]
  (println ((fileet [post title] "./test.txt") test-post "Post Template")))
