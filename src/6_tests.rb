require_relative 'age'
require_relative '6_framework_tests'

test_suite do
  test "Un guerrero tiene la energia con la que se lo instancio" do
    atila = Guerrero.new(20, 100, 10)

    assert(atila.energia == 100)
  end

  # usando la suite de tests para testear la suite de tests
  test "una suite de test corre el codigo de sus tests" do
    test_corrio = false

    mi_test_suite = TestSuite.new do
      test("test trivial") { test_corrio = true }
    end
    mi_test_suite.run(false)

    assert(test_corrio)
  end

  test "un test deberia frenarse al primer assert fallido" do
    ejecuto_mas_alla_del_primer_assert_fallido = false

    mi_test_suite = TestSuite.new do
      test("prueba") do
        assert(false)
        ejecuto_mas_alla_del_primer_assert_fallido = true
      end
    end
    mi_test_suite.run(false)

    assert(ejecuto_mas_alla_del_primer_assert_fallido == false)
  end
end

