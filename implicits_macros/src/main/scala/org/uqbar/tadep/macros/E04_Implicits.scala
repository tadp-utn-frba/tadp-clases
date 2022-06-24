package org.uqbar.tadep.macros.implicits

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Extension Methods
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

// Este método es demasiado específico para querer ponerlo en String
// Además, no podemos modificar la clase String...
def chequearSiEsUnMail(unString: String) =
  unString.length > 10 && unString.contains("@") && unString.endsWith(".com")

// extension (unString: String)
//   // Podemos "extender" los Strings que tengan esta extensión en contexto
//   def esUnMail = chequearSiEsUnMail(unString)

// val chequeo: Boolean = "foobar@gmail.com".esUnMail

// //Pero no afecta al polimorfismo...
// val unChequeable: { def esUnMail: Boolean } =
//   "un string cualquiera"
//   // new { def esUnMail = false }

// class StringExtension(unString: String):
//   def esUnMail = chequearSiEsUnMail(unString)
//
// val otroChequeo = new StringExtension("foobar@gmail.com").esUnMail

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Given Instances
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

trait BaseDeDatos

// class Persona:
//   def persistir(db: BaseDeDatos): Boolean = ???

// class Familia(padre: Persona, madre: Persona, hijos: List[Persona], abuelos: List[Persona]):
//   def persistir(db: BaseDeDatos) =
//     padre.persistir(db)
//     madre.persistir(db)
//     hijos.foreach(_.persistir(db))
//     abuelos.foreach(_.persistir(db))

// val miDB: BaseDeDatos   = null
// val unaFamilia: Familia = null
// val resultado           = unaFamilia.persistir(miDB)

// ──────────────────────────────────────────────────────────────────────────────────────────────────────────────────

// class Persona:
//   def persistir(using db: BaseDeDatos) = ???

// class Familia(padre: Persona, madre: Persona, hijos: List[Persona], abuelos: List[Persona]):
//   def persistir(using db: BaseDeDatos) =
//     padre.persistir
//     madre.persistir
//     hijos.foreach(_.persistir)
//     abuelos.foreach(_.persistir)

// given miDB: BaseDeDatos = null
// val unaFamilia: Familia = null
// val resultado           = unaFamilia.persistir
// val otroResultado       = unaFamilia.persistir(using miDB)

// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Implicit Conversions
// ═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

class Punto(x: Int, y: Int)
object GPS   { def nombreDelLugar(lugar: Punto): String = ??? }
object Input { def puntoPresionado: (Int, Int) = ???          }

// Pedirle al mapa el nombre del punto presionado
val nombre =
  val lugar = Input.puntoPresionado
  GPS.nombreDelLugar(new Punto(lugar._1, lugar._2))
// Sería lindo poder hacerlo así, pero una tupla no es un punto...
// GPS.nombreDelLugar(Input.puntoPresionado)

// Podemos usar esta función cada vez que queremos convertir una tupla en un punto
// def tuplaAPunto(lugar: (Int, Int)) = new Punto(lugar._1, lugar._2)

// val nombre = GPS.nombreDelLugar(tuplaAPunto(Input.puntoPresionado)) // Mejor...

// given Conversion[(Int, Int), Punto] = tupla => new Punto(tupla._1, tupla._2)

// ══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Type Classes
// ══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

object SQL   { def run(query: String) = ???                    }
object Redis { def guardar(clave: String, valor: String) = ??? }

// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
// Naive
// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────

// // SQL
// trait PersistibleConSQL:
//   def tabla: String
//   def valores: List[String]

// def persistirConSQL(obj: PersistibleConSQL) = SQL.run(s"INSERT INTO ${obj.tabla} VALUES ${obj.valores}")

// // Redis
// trait PersistibleConRedis:
//   def clave: String
//   def valor: String

// def persistirConRedis(obj: PersistibleConRedis) = Redis.guardar(obj.clave, obj.valor)

// // Dominio
// // Esta implementación es bastante invasiva.
// // Ensucia la interfaz y podrían haber conflictos entre los traits a implementar.
// // Además, una vez definida la clase no podríamos agregar más bases de datos.
// case class C(f1: String, f2: String) extends PersistibleConRedis with PersistibleConSQL:
//   // SQL
//   def tabla: String         = "Cs"
//   def valores: List[String] = List(f1, f2)
//   //Redis
//   def clave: String = "C"
//   def valor: String = s"{f1: $f1, f2: $f2}"

// object ejemploDeUso:
//   val c1 = new C("A", "1")
//   val c2 = new C("B", "2")
//   val c3 = new C("B", "3")
//   persistirConSQL(c1)
//   persistirConRedis(c2)
//   persistirConSQL(c3)
//   persistirConRedis(c3)

// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
// Extrayendo la lógica
// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────

// // Dominio
// case class C(f1: String, f2: String)

// // SQL
// trait PersistibleConSQL[T]:
//   def tabla(obj: T): String
//   def valores(obj: T): List[String]

// // Implementación de SQL para C
// object C_SQL extends PersistibleConSQL[C]:
//   def tabla(obj: C)   = "Cs"
//   def valores(obj: C) = List(obj.f1, obj.f2)

// def persistirConSQL[T](obj: T)(persistible: PersistibleConSQL[T]) =
//   SQL.run(s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}")

// object ejemploDeUso:
//   val c1 = new C("A", "1")
//   val c2 = new C("B", "2")
//   val c3 = new C("B", "3")
//   persistirConSQL(c1)(C_SQL)
//   persistirConSQL(c2)(C_SQL)
//   persistirConSQL(c3)(C_SQL)

// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
// Mejorando el uso con implicits
// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────

// // Dominio
// case class C(f1: String, f2: String)

// // SQL
// trait PersistibleConSQL[T]:
//   def tabla(obj: T): String
//   def valores(obj: T): List[String]

// // Implementación de SQL para C
// given PersistibleConSQL[C] with
//   def tabla(obj: C)   = "Cs"
//   def valores(obj: C) = List(obj.f1, obj.f2)

// def persistirConSQL[T](obj: T)(using persistible: PersistibleConSQL[T]) =
//   SQL.run(s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}")

// // def persistirConSQL[T: PersistibleConSQL](obj: T) =
// //   SQL.run(s"INSERT INTO ${summon[PersistibleConSQL[T]].tabla(obj)} VALUES ${summon[PersistibleConSQL[T]].valores(obj)}")

// object ejemploDeUso:
//   val c1 = new C("A", "1")
//   val c2 = new C("B", "2")
//   val c3 = new C("B", "3")
//   persistirConSQL(c1)
//   persistirConSQL(c2)
//   persistirConSQL(c3)

// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
// Mejorando un poquito más combinando con extensions
// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────

// // Dominio
// case class C(f1: String, f2: String)

// // SQL
// trait PersistibleConSQL[T]:
//   extension (obj: T)
//     protected def tabla: String
//     protected def valores: List[String]

//     def persistirConSQL[T: PersistibleConSQL]() =
//       SQL.run(s"INSERT INTO ${obj.tabla} VALUES ${obj.valores}")

// // Implementación de SQL para C
// given PersistibleConSQL[C] with
//   extension (obj: C)
//     protected def tabla   = "Cs"
//     protected def valores = List(obj.f1, obj.f2)

// object ejemploDeUso:
//   val c1 = new C("A", "1")
//   val c2 = new C("B", "2")
//   val c3 = new C("B", "3")
//   c1.persistirConSQL()
//   c2.persistirConSQL()
//   c3.persistirConSQL()
