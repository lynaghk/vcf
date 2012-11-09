(ns vcfvis.controls
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]]
               [dubstep.macros :only [publish! subscribe!]])
  (:use [chosen.core :only [ichooseu! options]]
        [singult.core :only [ignore]]
        [c2.core :only [unify]]
        [c2.util :only [clj->js]])
  (:require [vcfvis.core :as core]
            [vcfvis.histogram :as histogram]
            [vcfvis.data :as data]
            [c2.dom :as dom]
            [c2.event :as event]
            [goog.string :as gstring]))

;;;;;;;;;;;;;;;;;;
;;File multiselect
(def file-selector
  (let [$selector (dom/append! "#file-selector"
                               [:select {:multiple "multiple"
                                         :data-placeholder "Select VCF files"}])
        !c (ichooseu! $selector :search-contains true)]

    ;;The selector should always reflect the user's avaliable flies
    (constrain!
     (options !c (map (fn [{:keys [filename folder id]}]
                        {:text filename :value id :group folder})
                      @core/!available-files)))
    !c))

;;;;;;;;;;;;;;;;;;;;;;;
;;Metrics mini-hists
(bind! "#metrics"
       (let [shared (set (map :id @core/!shared-metrics))
             selected-metric @core/!metric
             metrics (for [m (vals (@core/!context :metrics))]
                       (assoc m
                         :selected? (= m selected-metric)
                         :visible? (core/visible-metric? m)
                         :shared? (contains? shared (m :id))))]
         [:div#metrics
          (unify metrics
                 (fn [{:keys [id desc selected? visible? shared?]}]
                   [:div.metric {:id (str "metric-" id)
                                 :class (str (when selected? "selected")
                                             " " (when visible? "expanded")
                                             " " (when-not shared?  "disabled"))}
                    [:h2 id]
                    [:button.expand-btn "V"]
                    [:span.desc desc]
                    [:div.mini-hist (ignore)]
                    [:div.sort-handle]])
                 :key-fn #(:id %))]))

(-> (js/jQuery "#metrics")
    (.sortable (js-obj "handle" ".sort-handle")))

(event/on "#metrics" :click
          (fn [d _ e]
            (when-not (dom/matches-selector? (.-target e) ".expand-btn")
              (core/select-metric! (dissoc d :selected? :shared? :visible?)))))

(event/on "#metrics" ".expand-btn" :click
          (fn [d]
            (let [m (dissoc d :selected? :shared? :visible?)]
              (when-not (core/visible-metric? m)
                ;;then it's about to become visible; draw the mini-hist
                (histogram/draw-mini-hist-for-metric! m))
              (core/toggle-visible-metric! m))))

(subscribe! {:filter-updated _}
            (histogram/draw-mini-hists!)
            (publish! {:count-updated (.value (get-in (first @core/!vcfs) [:cf :all]))}))

(subscribe! {:count-updated x}
            (dom/text "#count" x)
            (dom/style "#count" :visibility
                       (if (nil? x) "hidden" "visible")))

;; ## Filters
(bind! "#filters"
       (let [cs (reduce core/intersection (map :available-categories @core/!vcfs))]
         [:div#filters
          (unify cs
                 (fn [{:keys [id desc choices]}]
                   [:div.filter.metric {:id (str "filter-" id)}
                    [:span.desc desc]
                    (for [group-choices (partition-all 3 (sort choices))]
                      [:div.btn-group {:data-toggle "buttons-checkbox"}
                       (for [x group-choices]
                         [:btn.btn.filter-btn x])])]))]))

(defn update-category-filter!
  [cat-id val off?]
  (pp (str cat-id " " val " " off?)))

(event/on "#filters" :click
          (fn [d _ e]
            (update-category-filter! (:id d) (dom/text (.-target e))
                                     (gstring/contains (dom/attr (.-target e) :class) "active"))))

;;;;;;;;;;;;;;;;;;;;;;;
;;Download button
(let [$btn (dom/select "#filter-btn")]
  (bind! $btn
         (case (get @data/!analysis-status
                    (get (first @core/!vcfs) :file-url))
           :completed [:button#filter-btn.btn {:properties {:disabled true}} "Completed"]
           :running   [:button#filter-btn.btn {:properties {:disabled true}} "Running..."]
           nil        [:button#filter-btn.btn {:properties {:disabled false}} "Export subset"]))

  (event/on-raw $btn :click
                (fn [_]
                  (data/filter-analysis (get (first @core/!vcfs) :file-url)
                                        @core/!filters))))
