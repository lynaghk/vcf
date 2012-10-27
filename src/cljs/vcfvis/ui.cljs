(ns vcfvis.ui
  (:use-macros [c2.util :only [pp p bind!]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]
            [crate.core :as crate]))

(defn- logged-in-html [user]
  (crate/html
   [:div {:class "btn-group" :id "user-dropdown"}
    [:button {:class "btn btn-info dropdown-toggle" :data-toggle "dropdown"}
     [:i {:class "icon-user icon-white" :style "margin-right: 6px"}]
     user
     [:span {:class "caret" :style "margin-left: 6px"}]]
    [:ul {:class "dropdown-menu"}
     [:li [:a {:id "logout-btn" :href "/logout"} "Logout"]]]]))

(defn update-user!
  [context]
  (-> (dom/select "#user-dropdown")
      (dom/replace! (logged-in-html (get context :username "user")))))

;;;;;;;;;;;;;;;;;;;
;;Histogram params

(def hist-margin "left/right margin" 20)
(def inter-hist-margin "vertical margin between stacked histograms" 20)
(def axis-height (js/parseFloat (dom/style "#hist-axis" :height)))

(def hist-height
  "Height available to histogram facet grid"
  (js/parseFloat (dom/style "#histograms" :height)))

(def hist-width
  "Width of histogram facet grid"
  (- (js/parseFloat (dom/style "#histograms" :width))
     (* 2 hist-margin)))

(def hist-bins "number of histogram bins" 100)
