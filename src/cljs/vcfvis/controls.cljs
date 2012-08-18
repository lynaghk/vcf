(ns vcfvis.controls
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]])
  (:use [chosen.core :only [ichooseu! options]]
        [c2.core :only [unify]]
        [c2.util :only [clj->js]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]
            [c2.event :as event]))


;;;;;;;;;;;;;;;;;;
;;File multiselect
(def file-selector
  (let [$selector (dom/append! "#file-selector"
                               [:select {:multiple "multiple"
                                         :data-placeholder "Select VCF files"}])
        !c (ichooseu! $selector)]

    ;;The selector should always reflect the user's avaliable flies
    (constrain!
     (options !c (map (fn [{:keys [filename id]}]
                        {:text filename :value id})
                      @core/!available-files)))
    !c))




;;;;;;;;;;;;;;;;;;;;;;;
;;Metrics mini-hists
(bind! "#metrics"
       (let [shared (set (map :id @core/!shared-metrics))
             selected-metric @core/!metric
             metrics (for [[id m] (@core/!context :metrics)]
                       (assoc m :id id
                              :selected? (= m selected-metric)
                              :shared? (contains? shared id)))]
         [:div#metrics
          (unify metrics
                 (fn [{:keys [id desc selected? shared?]}] 
                   [:div.metric {:id (str "metric-" id)
                                 :class (str (when selected? "selected")
                                             " " (when-not shared?  "disabled"))}
                    [:h2 id]
                    [:span desc]
                    [:div.mini-hist]]))]))

(event/on "#metrics" :click core/select-metric!)






;;;;;;;;;;;;;;;;;;;;;;;
;;TODO: download button

;; (case (get @data/!analysis-status (vcf :filename))
;;                  :completed  [:button.btn {:properties {:disabled true}} "Completed"]
;;                  :running    [:button.btn {:properties {:disabled true}} "Running..."]
;;                  nil         [:button.btn {:properties {:disabled false}} "Export subset"])
