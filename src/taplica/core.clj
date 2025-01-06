(ns taplica.core
  "Extremely simple wrapper for `tap>` handling. The tap functions ([[tap!]] and [[tap>>]])
  do the tap registration (`add-tap`) if needed. The tapped values are stored into an
  atom. Note that while [[tap!]] and [[tap>>]] are wrappers for `tap>`, the regular `tap>`
  calls do not update the atom.")

(defonce ^:dynamic ^:private  *ctrl* (atom {}))
(defonce ^:dynamic
  ^{:doc "Taplica atom. Typically, there is no need to access the atom
  directly, but it is exposed just in case."}
  *taplica* (atom {}))

(defn- tap
  [{:keys [path value overwrite?] :as m}]
  (when (::taplica? m)
    (swap! *taplica* update path
           (fn [v]
             (if overwrite?
               [value]
               (conj (or v []) value)))))
  nil)


(defn- ->path [args]
  (-> args flatten vec))

(defn- tap-and-add-if-needed
  [overwrite? args]
  (let [path                   (->path (butlast args))
        value                  (last args)
        {:keys [state added?]} @*ctrl*]
    (case state
      :stopped (throw (ex-info "Taplica stopped." {}))
      :paused  nil
      (do
        (when-not added?
          (add-tap tap)
          (swap! *ctrl* assoc :added? true))
        (tap> {:path       path
               :value      value
               :overwrite? overwrite?
               ::taplica?  true})))
    value))

(defn tap!
  "Replaces the currently tapped value for the value path. Last arg is the value and the
  preceding items are considered its path. If there is only one (value) argument, it is
  stored into an empty path `[]`. Returns tapped value (or throws if stopped)."
  [& args]
  (tap-and-add-if-needed true args))

(defn tap>>
  "Adds the tapped value to the earlier taps for the same path. Last arg is the value and
  the preceding items are considered its path. If there is only one (value) argument, it
  is stored into empty path `[]`. Returns tapped value (or throws if stopped)."
  [& args]
  (tap-and-add-if-needed false args))

(defn pause
  "Removes tap and does not add it on subsequent [[tap!]] or [[tap>>]] calls."
  []
  (remove-tap tap)
  (swap! *ctrl* assoc :added? false :state :paused)
  nil)

(defn stop
  "Removes tap and throws on subsequent [[tap!]] or [[tap>>]] calls. This is sometimes
  useful to make sure that no taps are forgotten in the code."
  []
  (remove-tap tap)
  (swap! *ctrl* assoc :added? false :state :stopped)
  nil)

(defn resume
  "Clears pause or stop state."
  []
  (swap! *ctrl* dissoc :state)
  nil)

(defn reset
  "Reset Taplica to its initial state. No taps, no data."
  []
  (remove-tap tap)
  (reset! *ctrl* {})
  (reset! *taplica* {}))

(defn value
  "Tapped value list stored in `path`. With no arguments retuns the values in an empty
  path."
  [& path]
  (get @*taplica* (->path path)))

(defn values
  "Full Taplica atom contents."
  []
  @*taplica*)

(def fvalue
  "Convenience function for getting the _first_ value."
  (comp first value))

(def lvalue
  "Convenience function for getting the _last_ value."
  (comp last value))
