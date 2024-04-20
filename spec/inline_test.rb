test_suite do
  test do
    pepe = objeto do
      def nombre
        "Pepe"
      end

      def saludar
        "Hola #{nombre}"
      end
    end
    assert(pepe.saludar == "Hola Pepe")
  end

  test do
    Persona = clase do
      attr_reader :nombre

      def initialize(nombre)
        @nombre = nombre
      end
    end
    pepe = Persona.new("Pepe")
    assert(pepe.nombre == "Pepe")
  end
end