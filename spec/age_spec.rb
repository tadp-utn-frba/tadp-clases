require 'rspec'
require_relative '../src/age'

describe 'age of empires tests' do

  #Los guerreros tienen estos parámetros por default:
  # potencial_ofensivo=20, energia=100, potencial_defensivo=10
  it 'vikingo ataca a atila' do
    atila= Guerrero.new
    vikingo = Guerrero.new 70

    vikingo.atacar atila
    expect(atila.energia).to eq 90
  end

  it 'espadachin ataca a atila' do
    atila= Guerrero.new
    don_quijote = Espadachin.new(Espada.new(50))

    don_quijote.atacar atila
    expect(atila.energia).to eq(40)
    expect(don_quijote.energia).to eq(70)
  end

  it 'atila ataca a vikingo pero no le hace danio' do
    atila= Guerrero.new
    vikingo = Guerrero.new 70

    atila.atacar vikingo
    expect(atila.energia).to eq(100)
  end

  it 'Muralla solo defiende' do
    muralla = Muralla.new
    vikingo = Guerrero.new 70

    vikingo.atacar(muralla)
    expect(muralla.energia).to eq(180)
    vikingo.atacar(muralla)
    expect(muralla.energia).to eq(160)
  end

  it 'Muralla no ataca' do
    muralla = Muralla.new
    don_quijote = Espadachin.new(Espada.new(40))
    #Esto es la manera sintáctica de decir que lo que se espera dentro de los {} del expect, lanza una exception.
    #Despues veremos que quieren decir los {}
    expect {muralla.atacar don_quijote}.to raise_error(NoMethodError)
  end

  it 'Misil no defiende' do
    misil = Misil.new
    don_quijote = Espadachin.new(Espada.new(40))
    expect {don_quijote.atacar misil}.to raise_error(NoMethodError)
  end

  ######################

  it 'Atacante descansado pega doble' do
    atila = Guerrero.new #(potencial_ofensivo = 20, energia = 100, potencial_defensivo = 10)
    conan = Guerrero.new

    atila.descansar
    atila.atacar conan

    # 100 - (20 * 2 - 10)
    expect(conan.energia).to eq(70)
  end
  #
  it 'Atacante descansado ataca doble solo una vez por descanso' do
    atila = Guerrero.new
    conan = Guerrero.new
    heman = Guerrero.new

    atila.descansar
    atila.atacar conan
    atila.atacar heman

    # 100 - (20 - 10)
    expect(heman.energia).to eq(90)
  end

  it 'Un misil descansado tiene el doble de potencial ofensivo' do
    unMisil = Misil.new

    unMisil.descansar

    expect(unMisil.potencial_ofensivo).to eq(2000)
  end
  #
  it 'Defensor descansado suma 10' do
    muralla = Muralla.new
    expect(muralla.energia).to eq(200)

    muralla.descansar

    expect(muralla.energia).to eq(210)
  end
  #
  it 'Guerrero descansa como Defensor y como Atacante' do
    atila = Guerrero.new #(potencial_ofensivo = 20, energia = 100, potencial_defensivo = 10)
    conan = Guerrero.new
    expect(atila.energia).to eq(100)

    atila.descansar
    atila.atacar conan

    expect(conan.energia).to eq(70)
    expect(atila.energia).to eq(110)
  end
  #
  it 'kamikaze pierde su energia luego de atacar' do
    kamikaze = Kamikaze.new #(potencial_ofensivo = 250, energia = 100, potencial_defensivo = 10)
    muralla = Muralla.new #(potencial_defensivo = 50, energia = 200)

    kamikaze.atacar(muralla)

    expect(muralla.energia).to eq(0)
    expect(kamikaze.energia).to eq(0)
  end
  #
  it 'kamikaze descansa solo como atacante' do
    kamikaze = Kamikaze.new

    expect(kamikaze.potencial_ofensivo).to eq(250)

    kamikaze.descansar

    expect(kamikaze.energia).to eq(100)
    expect(kamikaze.potencial_ofensivo).to eq(500)
  end
  #
  # ######################
  #
  it('Peloton descansador hace descansar a sus guerreros que no estan descansados') do
    atila = Guerrero.new
    vikingo = Guerrero.new 100, 70
    don_quijote = Espadachin.new(Espada.new(50))
    Peloton.descansador(atila, vikingo)

    don_quijote.atacar(vikingo)
    #Al ser atacado, el vikingo le avisa al ejercito. El vikingo no esta descansado luego de recibir el ataque
    expect(atila.energia).to eq(100) #Atila no descansa, porque esta descansado
    expect(vikingo.energia).to eq(50) #En un test anterior energia quedaba en 40, pero ahora como descanso queda en 50
    expect(vikingo.potencial_ofensivo).to eq(140) #Ademas, su potencial_ofensivo se duplica (para el proximo ataque)
  end
  #
  it 'Peloton cobarde se retira cuando sufre danio uno de sus guerreros' do
    atila = Guerrero.new
    vikingo = Guerrero.new 70
    peloton = Peloton.cobarde(atila)

    vikingo.atacar(atila)

    expect(peloton.retirado).to be(true)
  end

  it 'Peloton estrategico' do
    atila = Guerrero.new
    vikingo = Guerrero.new 100, 70
    peloton = Peloton.estrategico(atila)

    vikingo.atacar(atila)

    expect(peloton.retirado).to be(true)
    expect(atila.potencial_ofensivo).to eq(20)
    peloton.retirado = false
    vikingo.atacar(atila)
    expect(atila.potencial_ofensivo).to eq(40)
    expect(peloton.retirado).to eq(false)
  end

  it 'Peloton patotero' do
    atila = Guerrero.new
    vikingo = Guerrero.new 100, 70
    don_quijote = Espadachin.new(Espada.new(50))

    giordano = Guerrero.new
    peloton = Peloton.patotero(giordano, [atila, vikingo])

    don_quijote.atacar(atila)

    expect(giordano.energia).to be(30)
  end
end