(ns ^:figwheel-hooks testprint.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [clojure.core.async :as async :refer [chan  go go-loop]]))

(println "This text is printed from src/testprint/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn print-el [id]  (js/printJS id "html"))

(defn my-component
  [x y z]
  (reagent/create-class                 ;; <-- expects a map of functions
   {:display-name  "my-component"      ;; for more helpful warnings & errors
    :component-did-mount               ;; the name of a lifecycle function
    (fn [this]
      (swap! app-state assoc :the-component [x y z])
      (println "component-did-mount")) ;; your implementation
    :component-did-update              ;; the name of a lifecycle function
    (fn [this old-argv]                ;; reagent provides you the entire "argv", not just the "props"
      (println :updated)
      (print-el "my-component"))
    :reagent-render        ;; Note:  is not :render
    (fn []           ;; remember to repeat parameters
      (let [[x y z] (@app-state :the-component)]
        [:div {:id "my-component"} (str x " " y " " z)]))}))


(defn api-call! []
  (async/go
    (let [res (async/<! (async/timeout 200))]
      (swap! app-state assoc :the-component [(rand-int 100) (rand-int 100) (rand-int 100)]))))

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/testprint/core.cljs and watch it change!"]
   [my-component 1 2 3]
   [:button {:id "increment" :type "button"
             :on-click #(swap! app-state (fn [m] (update m :the-component (fn [xs] (mapv inc xs)))))}
    "increment"]
   [:button {:id "api-call" :type "button"
             :on-click #(api-call!)}
    "api-call"]
   [:button {:id "manual-print" :type "button"
             :on-click #(print-el "my-component")}
    "manual print"]])


(defn mount [el]
  (rdom/render [hello-world] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
