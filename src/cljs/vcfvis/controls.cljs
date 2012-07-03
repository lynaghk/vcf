(ns vcfvis.controls
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]])
  (:use [chosen.core :only [ichooseu! options selected]]
        [c2.core :only [unify]]
        [c2.util :only [clj->js]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]
            [c2.event :as event]))

(def num-datasets 2)

(def selectors
  (let [$selectors (dom/select ".file-selectors")]
    (doall (for [_ (range num-datasets)]
             (let [$sel (dom/append! $selectors
                                     [:div.file-selector
                                      [:div.colorbox]
                                      [:select]])
                   !c (ichooseu! (dom/select "select" $sel))]
               ;;The selectors should always reflect the user's avaliable flies
               (constrain! (options !c @core/!available-filenames))
               !c)))))

(bind! "#metrics"
       (let [shared @core/!shared-metrics]
         [:div#metrics
          (unify shared
                 (fn [{:keys [id desc]
                      :as metric}]
                   [:label.radio {:title id :data-content desc

                                  ;;Hack through Bootstrap's broken caching.
                                  ;;Can be removed once Singult#2 is resolved.
                                  :data-original-title id}
                    [:input {:type "radio" :name "metric-type"
                             :properties {:checked (= @core/!metric metric)}}]
                    id])
                 :force-update? true)]))

(-> (js/$ "#metrics")
    (.popover (clj->js {:selector "label"
                        :placement "left"})))

(event/on "#metrics" :click core/select-metric!)
