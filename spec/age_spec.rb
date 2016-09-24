require 'rspec'
require_relative '../src/age'

describe 'Peloton' do

  it('Peloton descansador hace descansar a sus guerreros que no estan descansados') do
    atila = Guerrero.new
    vikingo = Guerrero.new 70
    don_quijote = Espadachin.new(Espada.new(50))
    Peloton.descansador([atila, vikingo])

    don_quijote.atacar(vikingo)
    #Al ser atacado, el vikingo le avisa al ejercito. El vikingo no esta descansado luego de recibir el ataque
    expect(atila.energia).to eq(100) #Atila no descansa, porque esta descansado
    expect(vikingo.energia).to eq(50) #En un test anterior energia quedaba en 40, pero ahora como descanso queda en 50
    expect(vikingo.potencial_ofensivo).to eq(140) #Ademas, su potencial_ofensivo se duplica (para el proximo ataque)
  end

  it 'Peloton cobarde se retira cuando sufre danio uno de sus guerreros' do
    atila = Guerrero.new
    vikingo = Guerrero.new 70
    peloton = Peloton.cobarde([atila])

    vikingo.atacar(atila)

    expect(peloton.retirado).to be(true)
  end

end