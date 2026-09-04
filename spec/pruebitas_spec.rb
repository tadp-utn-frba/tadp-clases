# frozen_string_literal: true

require 'rspec'
require_relative '../src/clase/od_missing'

RSpec.describe 'Pruebitas' do
  context 'un guerrero intenta resolver un cubo rubik' do
    it('explota')do
      atila = Guerrero.new()

      expect(atila.resolver_un_cubo_rubik)
        .to raise_error(NoMethodError)
    end
  end
end
