require '../src/segundaParte/testing/testing_framework'

assert false
assert true

se_han_ejecutado_los_tests = false # dentro del bloque tengo acceso a este booleano
test_suite = TestSuite.new'Quiero que falle' do
  #implicito esta self.test()
  test do
    se_han_ejecutado_los_tests = true
  end
end

test_suite.ejecutar
assert se_han_ejecutado_los_tests