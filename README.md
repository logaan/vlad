# vlad

Vlad is an attempt at providing convenient and simple validations. Vlad is
purely functional. It makes no assumptions about your data. It can be used for
validating html post data just as well as it can be used to validate your
csv about cats.

Those coming from a Ruby on Rails background will be famliar with validations
poluted with conditional statements and models poluted with virtual attributes.
Vlad aims to avoid this mess.

To use vlad add the following to your `project.clj` `:dependencies`:

    [vlad "1.0.0"]

## Composition

Vlad lets you build complex validations through composition. `join` will return
a validation that checks each of it's arguments. `chain` will stop checking
once the first validation fails. This helps avoid overwhelming your users with
redundant error messages.

```clojure
(def common
  (join (present [:name])
        (present [:email])))

(def password
  (chain (present [:password]
         (join (length-in 6 128   [:password])
               (match #"[a-zA-Z]" [:password])
               (match #"[0-9]"    [:password]))
         (equals-field [:password] [:confirmation]))))

(def signup
  (join common password)

(def update
  common)
```

And of course all these validations could be run over any data. Whether you're
pulling it in from a web service, a database or a csv file somewhere.

## A simple example

Say you have an application with user accounts. Lets say information about
users is collected from three places:

- Bulk import of users from a legacy system
  - No passwords are present in the data
  - Users will be emailed asking them to choose a password
- A user manually signing up
  - Password and password confirmation must be filled in and must match
- A user editing their account details
  - Blank password and password confirmation fields are provided to the user
  - They may be used to change the password, but are not required
  - If they are filled in then they must match

And of course passwords will not be stored in the database. Instead a hashed
version of the password will be used.

From this simple example we can see that it's not appropriate to have
validations tied to our persistance model. Firstly because our input data does
not match our persistance and secondly because our validation rules are not
consistent.

Vlad does not tie validations to any specific error message. Nor does it expect
you to specify human readable field names up front. Instead these concerns are
taken care of as transformations of the raw error data. This decoupling enables
localisation and contextualised field names.

## Further reading

You can find vlad at https://clojars.org/vlad

The beautiful Marginalia annotated source can be found at
http://logaan.github.com/vlad/

## TODO

* The predicate checks seem to be backwards. Should they not return true to
  represent validity?
* It would be quite nice to have a method that wraps errors up in lists and
  nil values in empty lists.
* It would also be quite nice to auto generate the error objects from the
  method signatures as they seem to be identicle. 
* equals-value and equals-field are terrible names.

## License

Copyright © 2012 Logan Campbell

Distributed under the Eclipse Public License, the same as Clojure.

