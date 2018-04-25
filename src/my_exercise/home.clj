(ns my-exercise.home
  (:require [hiccup.page :refer [html5]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [my-exercise.us-state :as us-state]))

(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "Find my next election"]
   [:link {:rel "stylesheet" :href "default.css"}]])

(defn getting-started [_]
  [:div {:class "getting-started"}
   [:h1 "Getting started"]
   [:p "Thank you for applying to work at Democracy Works! "
    "This coding exercise is designed to show off your ability to program web applications in Clojure. "
    "You should spend no more than 2 hours on it and then turn it in to us "
    "by running the command " [:code "lein submit"] " and following the instructions it prints out. "
    "While we will be evaluating how much of the project you complete, we know that 2 hours isn't enough time to do a "
    "thorough and complete job on all of it, and we're not expecting you to. We just want to see what you get working "
    "in that amount of time."]
   [:p "It is a server-side web application written in Clojure and using the "
    [:a {:href "https://github.com/ring-clojure/ring"} "Ring"] ", "
    [:a {:href "https://github.com/weavejester/compojure"} "Compojure"] ", and "
    [:a {:href "https://github.com/weavejester/hiccup"} "Hiccup"] " libraries."
    "You should feel free to use other libraries as you see fit."]
   [:p "Right now the form below submits to a missing route in the app. To complete the exercise, do the following:"]
   [:ul
    [:li "Create the missing /search route"]
    [:li "Ingest the incoming form parameters"]
    [:li "Derive a basic set of OCD-IDs from the address (see below for further explanation)"]
    [:li "Retrieve upcoming elections from the Democracy Works election API using those OCD-IDs"]
    [:li "Display any matching elections to the user"]]
   [:p "You will get bonus points for:"
    [:ul
     [:li "Documenting your code"]
     [:li "Adding tests for your code"]
     [:li "Standardizing and/or augmenting the address data to derive more OCD division IDs (e.g. county and "
      "legislative districts)"]
     [:li "Noting additional features or other improvements you would make if you had more time"]]]])

(defn ocd-id-explainer [_]
  [:div {:class "ocd-id-explainer"}
   [:h2 "All about OCD-IDs"]
   [:ul
    [:li "OCD-IDs are "
     [:a {:href "http://opencivicdata.readthedocs.io/en/latest/data/datatypes.html"}
      "Open Civic Data division identifiers"]
     " and they look like this (for the state of New Jersey): "
     [:code "ocd-division/country:us/state:nj"]]
    [:li "A given address can be broken down into several OCD-IDs. "
     "For example an address in Newark, New Jersey would be associated with the following OCD-IDs:"]
    [:ul
     [:li [:code "ocd-division/country:us"]]
     [:li [:code "ocd-division/country:us/state:nj"]]
     [:li [:code "ocd-division/country:us/state:nj/county:essex"]]
     [:li [:code "ocd-division/country:us/state:nj/place:newark"]]]
    [:li "Not all of those are derivable from just an address (without "
     "running it through a standardization and augmentation service). "
     "For example, just having a random address in Newark doesn't tell us "
     "what county it is in. But we can derive a basic set of state and place "
     "(i.e. city) OCD-IDs that will be a good starting point for this project. "
     "This entails... "
     [:ul
      [:li "lower-casing the state abbreviation and appending it to "
       [:code "ocd-division/country:us/state:"]]
      [:li "creating a copy of the state OCD-ID"]
      [:li "appending " [:code "/place:"] " to it"]
      [:li "lower-casing the city value, replacing all spaces with underscores, and appending it to that."]]
     "Then you should supply " [:em "both"] " OCD-IDs to your election API "
     "request, separated by a comma as shown in the curl example below."]
    [:li "Elections can be retrieved from the Democracy Works elections API for a set of district divisions like so:"]
    [:ul
     [:li [:code "curl 'https://api.turbovote.org/elections/upcoming?district-divisions=ocd-division/country:us/state:nj,ocd-division/country:us/state:nj/place:newark'"]]
     [:li "The response will be in the "
      [:a {:href "https://github.com/edn-format/edn"}
       "EDN format"]
      " (commonly used in Clojure) by default, but you can request JSON by setting your request's Accept header to 'application/json' if you prefer"]]]])

(defn current-elections-link [_]
  [:div {:class "current-elections-link"}
   [:h2 "Current elections"]
   [:p "Depending on the time of year and whether it's an odd or even-numbered "
    "year, the number of elections in the system can vary wildly. "
    "We maintain an up-to-date "
    [:a {:href "https://github.com/democracyworks/dw-code-exercise-lein-template/wiki/Current-elections"}
     "list of OCD-IDs that should return an election"]
    " until the dates they are listed under. Please refer to that for example "
    "OCD-IDs that will return an election to your app."]])

(defn instructions [request]
  [:div {:class "instructions"}
   (getting-started request)
   (ocd-id-explainer request)
   (current-elections-link request)])

(defn address-form [_]
  [:div {:class "address-form"}
   [:h1 "Find my next election"]
   [:form {:action "/search" :method "post"}
    (anti-forgery-field)
    [:p "Enter the address where you are registered to vote"]
    [:div
     [:label {:for "street-field"} "Street:"]
     [:input {:id "street-field"
              :type "text"
              :name "street"}]]
    [:div
     [:label {:for "street-2-field"} "Street 2:"]
     [:input {:id "street-2-field"
              :type "text"
              :name "street-2"}]]
    [:div
     [:label {:for "city-field"} "City:"]
     [:input {:id "city-field"
              :type "text"
              :name "city"}]
     [:label {:for "state-field"} "State:"]
     [:select {:id "state-field"
               :name "state"}
      [:option ""]
      (for [state us-state/postal-abbreviations]
        [:option {:value state} state])]
     [:label {:for "zip-field"} "ZIP:"]
     [:input {:id "zip-field"
              :type "text"
              :name "zip"
              :size "10"}]]
    [:div.button
     [:button {:type "submit"} "Search"]]]])

(defn page [request]
  (html5
   (header request)
   (instructions request)
   (address-form request)))

;;
;;
;; New work starts HERE
;;
;;


;; Make the first OCD-ID from the state only.
(defn ocd-id1 [city state]
  (str "ocd-division/country:us/state:"
       (clojure.string/lower-case state)))

;; Make the second OCD-ID from both city and state, replacing spaces in the city
;; with underscores.
(defn ocd-id2 [city state]
  (str (ocd-id1 city state)
       "/place:"
       (clojure.string/lower-case
        (clojure.string/replace city #"\s" "_"))))


;; A very very simple function to just show what the API returned.
(defn show-all-elections [election]
  [:table
   (map (fn [key value]
          (html5 [:tr
                  [:td key]
                  [:td (str value)]]))
        (keys election)
        (vals election))])


;; Call the API from Democracy Works with all of the info entered in the form.
;;
;; Completed:
;; 1.  Ingest form parameters
;; 2.  Derive basic set of OCD-IDs from address
;; 3.  Retrieve upcoming elections from API
;; 4.  Display matching elections to user (in a very ugly way)
;; 5.  Define (some) tests
;;
;; Would do with more time
;; 1.  Make the election output more user friendly
;; 2.  If there were no election results, output an appropriate message instead
;;     showing nothing
;; 3.  Do something fancier to map addresses to OCD-IDs
;; 4.  Better exception handling (e.g. user inputs, API errors, etc.)
;; 5.  Add to current list of very simple tests.
(defn call-api [street street-2 city state zip]

  (let [id1 (ocd-id1 city state)
        id2 (ocd-id2 city state)
        result (http/get (str "https://api.turbovote.org/elections/upcoming?district-divisions="
                              id1 "," id2) {:accept :json})
        body (json/read-str (get-in result [:body]))]
    (map show-all-elections body)))


(defn searchpage [street street-2 city state zip]
  (html5
   (header [])
   (call-api street street-2 city state zip)
   (address-form [])
   ))
