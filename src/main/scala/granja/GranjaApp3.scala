package granja

object GranjaApp3 extends App {

  abstract class Printer[-T] {
    def print(t: T): Unit
  }

  class AnimalPrinter extends Printer[Animal] {
    override def print(t: Animal): Unit =
      println(s"Este animal pesa: ${t.peso}")
  }

  class VacaLocaPrinter extends Printer[VacaLoca] {
    override def print(t: VacaLoca): Unit = {
      println(s"Una vaca loca se ríe así: ${t.reite}")
    }
  }

  var printer: Printer[VacaLoca] = new VacaLocaPrinter
  printer.print(new VacaLoca)

  printer = new AnimalPrinter
  printer.print(new VacaLoca)

}
