# Taplica

An extremely simple wrapper for `tap>` that taps data into an
atom and makes it accessible from REPL. The idea is to provide
no-boilerplate tapping support without leaving Emacs and Cider.

> [!NOTE]
> Taplica is not yet in Clojars

## Usage
Taplica provides two tap functions: `tap>>` and `tap!`. Both take the
tapped value with an optional path and stores it into the Taplica
atom. The difference is that `tap>>` adds new values to the value list
but `tap!` overrides the old value with a new one.

**Note:** Although both tap functions ultimately call `tap>` (and
`add-tap` if needed), regular `tap>` calls _do not_ add data to the
Taplica atom.

Tapped values can be queried with `value` (path value list) and
`values` (whole atom contents) functions. Convenience functions
`fvalue` and `lvalue` are like `value`, but return either first or the
last value respectively.

`reset` function clears the atom and unregisters taps.

Tapping can paused with `pause` and stopped with `stop`. Whan paused
`tap>>` and `tap!` calls do nothing, but when stopped they throw an
exception. In both cases the tapping can be resumed with `resume` or `reset`.


```clj
> (require '[taplica.core :as t])
nil
> (t/tap>> "hello")
true
> (t/tap>> "world")
true
;; "hello" is path and "world" value
> (t/tap>> "hello" "world")
true
> (t/values)
{[] ["hello" "world"], ["hello"] ["world"]}
> (t/value)
["hello" "world"]
> (t/fvalue)
"hello"
> (t/lvalue)
"world"
> (t/value "hello")
["world"]
> (t/tap! "howdy")
true
> (t/tap! ["hello"] "again")
true
> (t/tap! :one :two 3)
true
> (t/tap>> [:one :two] 4)
true
> (t/values)
{[] ["howdy"], ["hello"] ["again"], [:one :two] [3 4]}
> (t/reset)
{}
> (t/values)
{}
()> (t/pause)
nil
> (t/tap>> :one 1)
nil
> (t/tap! :two 2)
nil
> (t/values)
{}
> (t/stop)
nil
> (t/tap>> :one 1)
Execution error (ExceptionInfo) at taplica.core/tap-and-add-if-needed (core.clj:30).
Taplica stopped.
> (t/tap! :two 2)
Execution error (ExceptionInfo) at taplica.core/tap-and-add-if-needed (core.clj:30).
Taplica stopped.
> (t/values)
{}
> (t/resume)
nil
> (t/tap>> :one 1)
true
> (t/tap! :two 2)
true
> (t/values)
{[:one] [1], [:two] [2]}
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
