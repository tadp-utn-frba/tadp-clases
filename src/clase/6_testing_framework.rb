# frozen_string_literal: true

class EjemploTest
  extend TadTest

  testear_que 'true es true' do
    assert(true)
  end

  # testear_que 'false es false' do
  #   assert(false)
  # end
  #
  # testear_que 'son iguales' do
  #   deberian_ser_iguales(10, 5*3)
  # end
  #
  # testear_que 'son eql' do
  #   deberian_ser_eql?(10, 5*3)
  # end
  #
  # testear_que 'cualquier cosa' do
  #   deberian_ser_cualquier_cosa?(10, 5*3)
  # end
end

EjemploTest.correr

module TadTest

end