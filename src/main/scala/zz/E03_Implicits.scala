package zz

object E03_Implicits {

  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
  // Implicit Class
  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

  class StringExtendido(unString: String) {
    // Este método es demasiado específico para querer ponerlo en String
    def esUnMail = unString.length > 10 && unString.contains("@") && unString.endsWith(".com")
  }

  new StringExtendido("foobar@gmail.com").esUnMail // Sí!
  new StringExtendido("Hola Mundo!").esUnMail // No!
  //  "foobar@gmail.com".esUnMail // Esto no anda...

  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
  // Implicit Conversions
  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

  class Punto(x: Int, y: Int)
  object Mapa { def nombreDelLugar(lugar: Punto): String = ??? }
  object Input { def puntoPresionado: (Int, Int) = ??? }

  // Pedirle al mapa el nombre del punto presionado 
  val lugar = Input.puntoPresionado
  Mapa.nombreDelLugar(new Punto(lugar._1, lugar._2))

  //	Mapa nombreDelLugar Input.puntoPresionado // Sería lindo poder hacerlo así, pero una tupla no es un punto...

  // Podemos usar esta función cada vez que queremos convertir una tupla en un punto
  def tuplaAPunto(lugar: (Int, Int)) = new Punto(lugar._1, lugar._2)

  Mapa nombreDelLugar tuplaAPunto(Input.puntoPresionado) // Mejor...

  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
  // Implicit Parameters
  //═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

  trait BaseDeDatos

  //	class Persona { def persistir(db: BaseDeDatos) = ??? }
  //	
  //	class Familia(padre: Persona, madre: Persona, hijos: List[Persona], abuelos: List[Persona]) {
  //		def persistir(db: BaseDeDatos) {
  //			padre.persistir(db)
  //			madre.persistir(db)
  //			hijos.foreach(_.persistir(db))
  //			abuelos.foreach(_.persistir(db))
  //		}
  //	}

  val miDB: BaseDeDatos = ???
  val unaFamilia: Familia = ???

  unaFamilia.persistir(miDB)

  //	class Persona { def persistir(implicit db: BaseDeDatos) = ??? }
  //	
  //	class Familia(padre: Persona, madre: Persona, hijos: List[Persona], abuelos: List[Persona]) {
  //		def persistir(implicit db: BaseDeDatos) {
  //			padre.persistir
  //			madre.persistir
  //			hijos.foreach(_.persistir)
  //			abuelos.foreach(_.persistir)
  //		}
  //	}

  class Persona { def persistir(implicit db: BaseDeDatos) = ??? }

  class Familia(padre: Persona, madre: Persona, hijos: List[Persona], abuelos: List[Persona]) {
    def persistir(implicit db: BaseDeDatos) {
      padre.persistir
      madre.persistir
      hijos.foreach(_.persistir)
      abuelos.foreach(_.persistir)
    }
  }

  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  // Type Classes
  //▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

  object SQL { def run(query: String) = ??? }
  object Redis { def guardar(clave: String, valor: String) = ??? }

  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
  // Naive
  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
  //
  //	// SQL
  //
  //	trait PersistibleConSQL {
  //		def tabla: String
  //		def valores: List[String]
  //	}
  //
  //	def persistirConSQL(obj: PersistibleConSQL) = {
  //		SQL run s"INSERT INTO ${obj.tabla} VALUES ${obj.valores}"
  //	}
  //
  //	// Redis
  //
  //	trait PersistibleConRedis {
  //		def clave: String
  //		def valor: String
  //	}
  //
  //	def persistirConRedis(obj: PersistibleConRedis) = {
  //		Redis guardar (obj.clave, obj.valor)
  //	}
  //
  //	// Dominio
  //
  //	case class C(f1: String, f2: String) extends PersistibleConRedis with PersistibleConSQL {
  //		def tabla = "C"
  //		def valores = List(f1, f2)
  //		def clave = "C"
  //		def valor = s"{f1: $f1, f2: $f2}"
  //
  //		// Es muy invasivo. Ensucia la interfaz y podrían haber conflictos entre los traits a implementar...
  //	}
  //
  //	// Uso
  //
  //	val c1 = new C("A", "1")
  //	val c2 = new C("B", "2")
  //	val c3 = new C("B", "3")
  //
  //	persistirConSQL(c1)
  //	persistirConRedis(c2)
  //	persistirConSQL(c3)
  //	persistirConRedis(c3)

  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
  // Extrayendo la lógica
  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
  //
  //	// SQL
  //	trait PersistibleConSQL[T] {
  //		def tabla(obj: T): String
  //		def valores(obj: T): List[String]
  //	}
  //
  //	def persistirConSQL[T](obj: T)(persistible: PersistibleConSQL[T]) = {
  //		SQL run s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}"
  //	}
  //
  //	// Dominio
  //
  //	case class C(f1: String, f2: String)
  //
  //	object CSQL extends PersistibleConSQL[C] {
  //		def tabla(obj: C) = "C"
  //		def valores(obj: C) = List(obj.f1, obj.f2)
  //	}
  //
  //	// Uso
  //	
  //	val c1 = new C("A", "1")
  //	val c2 = new C("B", "2")
  //	val c3 = new C("B", "3")
  //
  //	persistirConSQL(c1)(CSQL)
  //	persistirConSQL(c2)(CSQL)
  //	persistirConSQL(c3)(CSQL)

  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
  // Mejorando el uso con implicits
  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
  //
  //	// SQL
  //	trait PersistibleConSQL[T] {
  //		def tabla(obj: T): String
  //		def valores(obj: T): List[String]
  //	}
  //
  //	def persistirConSQL[T](obj: T)(implicit persistible: PersistibleConSQL[T]) = {
  //		SQL run s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}"
  //	}
  //
  //	// Dominio
  //
  //	case class C(f1: String, f2: String)
  //
  //	implicit object CSQL extends PersistibleConSQL[C] {
  //		def tabla(obj: C) = "C"
  //		def valores(obj: C) = List(obj.f1, obj.f2)
  //	}
  //
  //	// Uso
  //	
  //	val c1 = new C("A", "1")
  //	val c2 = new C("B", "2")
  //	val c3 = new C("B", "3")
  //
  //	persistirConSQL(c1)
  //	persistirConSQL(c3)

  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
  // Mejorando un poquito más usando implicitly
  //─────────────────────────────────────────────────────────────────────────────────────────────────────────────────

  //	// SQL
  //	trait PersistibleConSQL[T] {
  //		def tabla(obj: T): String
  //		def valores(obj: T): List[String]
  //	}
  //
  //	// def persistirConSQL[T](obj: T)(implicit persistible: PersistibleConSQL[T])
  //	def persistirConSQL[T: PersistibleConSQL](obj: T) = {
  //		val persistible = implicitly[PersistibleConSQL[T]]
  //		SQL run s"INSERT INTO ${persistible.tabla(obj)} VALUES ${persistible.valores(obj)}"
  //	}
  //
  //	// Redis
  //	trait PersistibleConRedis[T] {
  //		def clave(obj: T): String
  //		def valor(obj: T): String
  //	}
  //
  //	def persistirConRedis[T:PersistibleConRedis](obj: T) = {
  //		val persistible = implicitly[PersistibleConRedis[T]]
  //		Redis guardar (persistible.clave(obj), persistible.valor(obj))
  //	}
  //
  //	// Dominio
  //
  //	case class C(f1: String, f2: String)
  //	object C {
  //		implicit object CSQL extends PersistibleConSQL[C] {
  //			def tabla(obj: C) = "C"
  //					def valores(obj: C) = List(obj.f1, obj.f2)
  //		}
  //	}
  //
  //	implicit object CRedis extends PersistibleConRedis[C] {
  //		def clave(obj: C) = "C"
  //		def valor(obj: C) = s"{f1: ${obj.f1}, f2: ${obj.f2}}"
  //	}
  //
  //	// Uso
  //	
  //	val c1 = new C("A", "1")
  //	val c2 = new C("B", "2")
  //	val c3 = new C("B", "3")
  //
  //	persistirConSQL(c1)
  //	persistirConRedis(c2)
  //	persistirConSQL(c3)
  //	persistirConRedis(c3)

}