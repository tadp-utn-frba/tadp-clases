package org.uqbar.tadep.macros.macros

import scala.quoted.*

// Dada una expresión `exp`
// '{exp} convierte la expresión en el AST que la representa
// ${exp} evalua la expresión, que debe retornar un AST o una estructura de tipo
// Por lo tanto se cumple que:
// ${'{exp}} == exp
// '{${exp}} == exp

// Dado un tipo `T`
// Type.of[T] representa la estructura de tipo que lo representa

// TODO:
// Inline:
// For methods their body will be inserted in the place of the call (some constant-based expressions will even be evaluated).
// For expressions, they will be evaluated on compile time.
// For method parameters they will be inserted in the place of usage

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Id
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

inline def id[T](inline obj: T): T = ${idImplementation('obj)}

private def idImplementation[T](expr: Expr[T])(using Quotes): Expr[T] = expr

// ══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// printTree
// ══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

inline def printTree(inline code: Any): String = ${printTreeImplementation('code)}

private def printTreeImplementation(body: Expr[Any])(using qctx:Quotes): Expr[String] =
  import qctx.reflect.*

  def format(pending: String, done: String = "", level: Int = 0): String =
    def cr(lvl: Int) = "\n" + "  " * lvl
    pending.replaceAll(" ", "") match
      case "" => done
      case s"()$more" => format(more, done + "()", level)
      case s"($more" => format(more, done + "(" + cr(level + 1), level + 1)
      case s"),$more" => format(more, done + cr(level - 1) + ")," + cr(level - 1), level - 1)
      case s"))$more" => format(more, done + cr(level - 1) + ")" + cr(level - 2) + ")", level - 2)
      case s")$more" => format(more, done + cr(level - 1) + ")" + cr(level - 1), level - 1)
      case s",$more" => format(more, done + "," + cr(level), level)
      case more => format(more.tail, done + more.head, level)

  Expr(format(body.asTerm.show(using Printer.TreeStructure)))

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Assert
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

inline def assert(inline condition: Boolean): Unit = ${assertImplementation('condition)}

private def assertImplementation(expr: Expr[Boolean])(using Quotes): Expr[Unit] =
  val failMsg: Expr[String] = Expr("failed assertion: " + expr.show)
  '{ if !($expr) then throw new AssertionError($failMsg) }

// Podemos fallar en compilación, pero sólo funciona con expresiones constantes
// private def assertImplementation(expr: Expr[Boolean])(using Quotes): Expr[Unit] =
//   if !(expr.valueOrAbort) then throw new AssertionError("failed assertion: " + expr.show)
//   '{()}

// ══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Debug
// ══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

inline def debug(inline code: Any): Any = ${debugImplementation('code)}

private def debugImplementation(body: Expr[Any])(using qctx:Quotes): Expr[Any] =
  import qctx.reflect.*

  def label(expr: Expr[Any]): Expr[String] = Expr(s"[DEBUG]: ${expr.asTerm.show}")

  def insertLogs(body: Expr[Any]): List[Expr[Any]] = body match
      case '{ ${expr}:Any; $others } => '{println(${label(expr)})} :: expr :: insertLogs(others)
      case expr => List('{println(${label(expr)})}, expr)

  val sentences = insertLogs(body)
  Expr.block(sentences.init, sentences.last)

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Ejemplo con validación
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

case class Email(id: String, domain: String)


inline def email(str: String): Email = ${emailImplementation('str)}
private def emailImplementation(str: Expr[String])(using qctx:Quotes): Expr[Email] =
  val emailFormat = """(\w{4,})@([\w\.]+.com)""".r
  str.valueOrAbort match
    case emailFormat(id, domain) =>
      val idExpr = Expr(id)
      val domainExpr = Expr(domain)
      '{Email($idExpr, $domainExpr)}
    case _ => throw new Exception("Invalid email format")

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Poniendo todo junto
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

extension (inline context: StringContext)
  inline def mail(inline args: String*): Email = ${mailImplementation('context, 'args)}

// private def mailImplementation(context: Expr[StringContext], args: Expr[Seq[Any]])(using qctx:Quotes): Expr[Email] = '{
//   val emailFormat = """(\w{4,})@([\w\.]+.com)""".r
  
//   $context.s($args:_*) match
//     case emailFormat(id, domain) => Email(id, domain)
//     case _ => throw new Exception("Invalid email format")
// }

private def mailImplementation(context: Expr[StringContext], args: Expr[Seq[String]])(using qctx:Quotes): Expr[Email] =
  
  val emailFormat = """(\w{4,})@([\w\.]+.com)""".r
  val mail = context.valueOrAbort.s(args.valueOrAbort:_*)

  mail match
    case emailFormat(id, domain) => '{Email(${Expr(id)}, ${Expr(domain)})}
    case _ => throw new Exception("Invalid email format")