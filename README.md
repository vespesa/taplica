# Taplica

[![Clojars Project](https://img.shields.io/clojars/v/com.github.vespesa/taplica.svg)](https://clojars.org/com.github.vespesa/taplica)
[![cljdoc badge](https://cljdoc.org/badge/com.github.vespesa/taplica)](https://cljdoc.org/d/com.github.vespesa/taplica)

An extremely simple wrapper for `tap>` that taps data into an atom and
makes it accessible from REPL. The idea is to provide no-boilerplate
tapping support without leaving Emacs and
[Cider](https://github.com/clojure-emacs/cider).

## Installing
It is often a good idea to make Taplica available automatically for
every project. For example, with [Leiningen](https://leiningen.org/)
using [lein-shorthand](https://github.com/palletops/lein-shorthand) in
`~/.lein/profiles.clj`:

```clj
{:user {:dependencies [[com.github.vespesa/taplica "0.1.0"]]
        :plugins      [[com.gfredericks/lein-shorthand "0.4.1"]]
        :shorthand    {tap [taplica.core/tap>> taplica.core/tap!
                            taplica.core/values taplica.core/value
                            taplica.core/fvalue taplica.core/lvalue
                            taplica.core/pause taplica.core/stop
                            taplica.core/resume taplica.core/reset]}}}
```
Now the listed functions are always available under `tap` namespace
alias (`tap/tap>>`, `tap/tap!`, ...).

## Usage
Taplica provides two tap functions: `tap>>` and `tap!`. Both take the
tapped value with an optional path and store it into the Taplica
atom. The difference is that `tap>>` adds new values to the value list
but `tap!` overrides the old value with a new one. Both functions
return the tapped value so they can be easily used with threading
macros.

> [!NOTE]
> Although both tap functions ultimately call `tap>` (and `add-tap` if
> needed), regular `tap>` calls _do not_ add data to the Taplica atom.

Tapped values can be queried with `value` (path value list) and
`values` (whole atom contents) functions. Convenience functions
`fvalue` and `lvalue` are like `value`, but return either first or the
last value respectively.

`reset` function clears the atom and unregisters taps.

Tapping can paused with `pause` and stopped with `stop`. When paused
`tap>>` and `tap!` calls do nothing, but when stopped they throw an
exception. In both cases the tapping can be resumed with `resume` or
`reset`.

### `tap>>` vs. `tap!`

```clj
> (tap/tap>> :one "alice")
"alice"
> (tap/tap>> :one "bob")
"bob"
> (tap/tap! :two "charlie")
"charlie"
> (tap/tap! :two "diana")
"diana"
> (tap/values)
{[:one] ["alice" "bob"], [:two] ["diana"]}
```

### `value`, `fvalue` and `lvalue`

```clj
> (tap/value)
nil
> (tap/value :one)
["alice" "bob"]
> (tap/fvalue :one)
"alice"
> (tap/lvalue [:one])
"bob"
```

### `pause` vs. `stop`
```clj
> (tap/pause)
nil
> (tap/tap>> :three "eric")
"eric"
> (tap/value :three)
nil
> (tap/reset)
{}
> (tap/tap>> :three "eric")
"eric"
> (tap/values)
{[:three] ["eric"]}
> (tap/stop)
nil
> (tap/tap! :four "faye")
Execution error (ExceptionInfo) at taplica.core/tap-and-add-if-needed (core.clj:33).
Taplica stopped.
> (tap/resume)
nil
> (tap/tap! :four "faye")
"faye"
> (tap/values)
{[:three] ["eric"], [:four] ["faye"]}
```


## License

Copyright © 2024 Vespe Savikko

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
