import scala.reflect.runtime.universe._
 
object symbols_class_4 {
	// Más adelante vemos cómo conseguirlo
  val classSymbol: ClassSymbol = ???              //> scala.NotImplementedError: an implementation is missing
                                                  //| 	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:225)
                                                  //| 	at symbols_class_4$$anonfun$main$1.apply$mcV$sp(symbols_class_4.scala:5)
                                                  //| 
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at symbols_class_4$.main(symbols_class_4.scala:3)
                                                  //| 	at symbols_class_4.main(symbols_class_4.scala)

	// returns false
  classSymbol.isCaseClass
  
  // returns false
  classSymbol.isModule
  
  // returns false
  classSymbol.isTrait
  
  // returns object C : Symbol
  classSymbol.companion
  
  // returns true
  classSymbol.isPublic
  
  // returns List(type T)
  classSymbol.typeParams
  
  // returns the Type
  classSymbol.toType
  
  // returns C : TypeName
  classSymbol.name
  
  // returns constructor C : Symbol
  classSymbol.primaryConstructor

}