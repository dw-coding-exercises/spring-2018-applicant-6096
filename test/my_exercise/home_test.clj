(ns my-exercise.home-test
  (:require [clojure.test :refer :all]
            [my-exercise.home :refer :all]))

(deftest ocd-tests
  (testing "ocd-id1 produces an OCD-ID from lower-case state string"
    (is (= "ocd-division/country:us/state:nj" (ocd-id1 "Newark" "NJ"))))
  (testing "ocd-id2 produces an OCD-ID from lower-case city string"
    (is (= "ocd-division/country:us/state:nj/place:newark" (ocd-id2 "Newark" "NJ"))))
  (testing "ocd-id2 produces an OCD-ID from city string with spaces"
    (is (= "ocd-division/country:us/state:nj/place:garden_city" (ocd-id2 "Garden City" "NJ"))))
  )
