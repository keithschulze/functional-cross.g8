package $organization$

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object $name;format="Camel"$Specification extends Properties("$name$") {

  property("startsWith") = forAll {
    (a: String, b: String) =>
      (a + b).startsWith(a)
  }
}


