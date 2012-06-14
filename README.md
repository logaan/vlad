# vlad

Vlad is an attempt at providing convenient and simple validations. Vlad is
purely functional and makes no assumptions about your data. It can be used for
validating html form data as just as well as it can be used to validate your
csv about cats.

The beautiful Marginalia annotated source can be found at
http://logaan.github.com/vlad/

## Rant

*Please note that the code displayed here is not valid Vlad code*

Validations don't belong on the model. It's quite a common case that you've got
different requirements for what counts as valid data depending on where it's
coming from. Like needing a password to be input on account creation, storing
it in a virtual field and validating against it but then storing the encrypted
password in another field. When you come to edit your account details you're
most likely not going to need the password to be enterted again. Instead I
think that validations need to be thought of as being more closely tied to
forms (or other input methods) or by themselves.

If we have validations sitting by themselves we're also able to compose them
and to write meta validations. So you could have:

### Composition

    (def common
      (required :name, :email))

    (def signup
      (reduce join
        (required :password)
        (confirm  :password)
        common)

    (def update
      common)

### Meta

    (def standard-signup
      all-fields-required   ; makes sure the data has no nil values
      no-curse-words        ; keep things polite across all fields
      email-fields          ; expects fields with email in the name to be emails
      password-fields)      ; ditto for passwords

Once you've introduced the idea of composing validations you can even have
different kinds of composition. Perhaps you want to avoid overwhelming users
with validation errors and instead want to cater for the common cases and only
present the edge cases if they crop up. There's not much point telling someone
their password is the wrong length and that it's required:

    (def password
      (chain
        required
        (reduce join
          (length :gt 6 :lt 128)
          (match #"[a-zA-Z]")
          (match #"[0-9]"))))

And of course all these validations could be run over any data. Whether you're
pulling it in from a web service, a database or a csv file somewhere.

## Installation

Once some more work has been done I intend to make vlad available through
clojars.

## License

Copyright © 2012 Logan Campbell

Distributed under the Eclipse Public License, the same as Clojure.
