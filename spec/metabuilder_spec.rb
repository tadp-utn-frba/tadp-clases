require 'rspec'
require_relative '../src/metabuilder'

describe 'Metabuilder' do

  class Perro
    attr_accessor :raza, :edad, :duenio

    def initialize
      @duenio = 'Cesar Millan'
    end

  end

  it 'puedo crear un builder de perros' do
    #con el metabuilder definimos un builder
    metabuilder = Metabuilder.new
    metabuilder.set_class(Perro)
    metabuilder.set_properties(:raza, :edad, :duenio)
    builder_de_perros = metabuilder.build


    builder_de_perros.set_property(:raza, 'Fox Terrier')
    builder_de_perros.set_property(:edad, 4)
    builder_de_perros.set_property(:duenio, 'Pepe')
    perro = builder_de_perros.build

    expect(perro.raza).to eq('Fox Terrier')
    expect(perro.edad).to eq(4)
    expect(perro.duenio).to eq('Pepe')

    builder_de_perros.set_property(:raza, 'York')
    builder_de_perros.set_property(:duenio, 'Cesar')
    perro_york = builder_de_perros.build

    expect(perro_york.raza).to eq('York')
    expect(perro_york.edad).to eq(4)
    expect(perro_york.duenio).to eq('Cesar')

  end

  it 'puedo crear un builder de perros con method missing' do
    metabuilder = Metabuilder.new
    metabuilder.set_class(Perro)
    metabuilder.set_properties(:raza, :edad)
    builder_de_perros = metabuilder.build

    builder_de_perros.set_property :raza, 'Fox Terrier'
    builder_de_perros.raza= 'Fox Terrier'
    builder_de_perros.edad= 4

    expect {
      builder_de_perros.saraza = 10
    }.to raise_error NoMethodError

    perro = builder_de_perros.build

    expect(perro.raza).to eq('Fox Terrier')
    expect(perro.edad).to eq(4)
    expect(perro.duenio).to eq('Cesar Millan')
  end


  it 'puedo definir validaciones que rompen' do
    #Más codigo va aca
    metabuilder = Metabuilder.new
    metabuilder.set_class(Perro)
    metabuilder.set_properties(:raza, :edad)
    metabuilder.validate {
      ['Fox Terrier', 'San Bernardo'].include? raza
    }
    metabuilder.validate {
      edad > 0 && edad < 20
    }

    builder_de_perros = metabuilder.build

    builder_de_perros.raza = 'Fox Terrier'
    builder_de_perros.edad = -5
    expect {
      builder_de_perros.build
    }.to raise_error ValidationError
  end


  it 'puedo definir validaciones que pasan' do
    metabuilder = Metabuilder.new
    metabuilder.set_class(Perro)
    metabuilder.set_properties(:raza, :edad)
    metabuilder.validate {
      ['Fox Terrier', 'San Bernardo'].include? raza
    }
    metabuilder.validate {
      edad > 0 && edad < 20
    }

    builder = metabuilder.build
    builder.raza = 'Fox Terrier'
    builder.edad = 4
    perro = builder.build

    expect(perro.raza).to eq('Fox Terrier')
    expect(perro.edad).to eq(4)
  end

  it 'Puedo definir Metabuilder de clases que aun no existen' do
    metabuilder = Metabuilder.new
    metabuilder.create_class :Gato do
      attr_accessor :raza, :pelaje
    end

    metabuilder.set_properties(:raza, :pelaje)

    builder_gato = metabuilder.build
    builder_gato.raza = 'Siames'
    builder_gato.pelaje = 'corto'
    gato = builder_gato.build

    expect(gato.raza).to eq('Siames')
  end

  # it 'agrega metodos cuando se cumple la condicion' do
  #   #Más codigo va aca
  #   metabuilder.conditional_method(
  #       :caza_un_zorro,
  #       proc {
  #         raza == 'Fox Terrier' && edad > 2
  #       },
  #       proc {
  #         "Ahora voy #{duenio}"
  #       }
  #   )
  #
  #   builder1 = metabuilder.build
  #   builder1.raza = 'Fox Terrier'
  #   builder1.edad = 3
  #   fox_terrier = builder1.build
  #
  #   expect(fox_terrier.caza_un_zorro).to eq('Ahora voy Cesar Millan')
  #
  #   builder2 = metabuilder.build
  #   builder2.raza = 'San Bernardo'
  #   builder2.edad = 3
  #   san_bernardo = builder2.build
  #
  #   expect {
  #     san_bernardo.caza_un_zorro
  #   }.to raise_error(NoMethodError)
  # end
  #
  # it 'puede agregar métodos con parámetros' do
  #   #Más codigo va aca
  #   metabuilder.conditional_method(
  #       :rescata_a,
  #       proc { raza == 'San Bernardo' },
  #       proc { |nombre|
  #         "Rescate a #{nombre}!"
  #       })
  #
  #   builder_de_perros = metabuilder.build
  #   builder_de_perros.raza = 'San Bernardo'
  #   builder_de_perros.edad = 2
  #   perro = builder_de_perros.build
  #
  #   expect(perro.method(:rescata_a).arity).to eq(1)
  #   expect(perro.rescata_a('Pedro')).to eq('Rescate a Pedro!')
  # end
  #

end