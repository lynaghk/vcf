;; Generally useful user interface functionality
(ns aahru.ui
  (:use-macros [c2.util :only [pp]])
  (:require [clojure.string :as string]
            [c2.dom :as dom]
            [crate.core :as crate]
            [shoreleave.remotes.http-rpc :as rpc]))

(defn ^:export set-navigation
  "Correctly set the active top level navigation toolbar."
  []
  (let [loc (-> (.toString window.location ())
                (string/split #"/")
                last)]
    (doseq [list-item (dom/children (dom/select "#top-navbar"))]
      (if (= (str "/" loc)
             (-> (dom/children list-item)
                 first
                 (dom/attr :href)))
        (dom/add-class! list-item "active")
        (dom/remove-class! list-item "active")))))

(defn- logged-in-dropdown [user]
  (crate/html
   [:ul {:class "nav pull-right" :id "user-dropdown"}
    [:li {:class "divider-vertical"}]
    [:li {:class "dropdown"}
     [:a {:href "#" :class "dropdown-toggle" :data-toggle "dropdown"}
      [:i {:class "icon-user icon-white" :style "margin-right: 6px"}]
      user
      [:span {:class "caret" :style "margin-left: 6px"}]]
     [:ul {:class "dropdown-menu"}
      [:li [:a {:id "logout-btn" :href "/logout"} "Logout"]]]]]))

(defn ^:expose set-user
  "Set user information with dropdown in top level navigation toolbar."
  []
  (rpc/remote-callback "meta/username" []
                       (fn [username]
                         (when username
                           (dom/replace! "#user-dropdown"(logged-in-dropdown username))))))
