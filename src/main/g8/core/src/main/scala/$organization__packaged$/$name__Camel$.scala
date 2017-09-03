package $organization$

import cats.effect.IO

/**
 * Main stub
 * You should probably change this or get rid of it!
 */
object $name;format="Camel"$ {

  def putStrLn(line: String): IO[Unit] =
    IO { println(line) }

  def main(args: Array[String]): Unit = {
    putStrLn("hello $name$!").unsafeRunAsync(cb => cb match {
      case Right(_) => ()
      case Left(t)  => println(t.getMessage())
    })
  }
}
