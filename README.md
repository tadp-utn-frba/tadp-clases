# Intro Multimethods

Ejemplo introductorio en XTend

**Method lookup básico:**

```java
abstract class Trabajador {
    trabajar() {
        System.out.println("No hago nada!!");
    }
}

class Artesano extends Trabajador {
    trabajar() {
        System.out.println("Hice un duendecito!");
    }
}

class Zapatero extends Trabajador {
    trabajar(String tipoZapato) {
        System.out.println("Hice un " + tipoZapato);
    }
}

class Taller {
    trabajar(Trabajador t) {
        t.trabajar()
    }
}
```

**Con multimethods:**

```java
class Taller {
    trabajar(Artesano a) {
        System.out.println("Hice un duendecito!");
    }

    trabajar(Zapatero z, String tipoZapato) {
        System.out.println("Hice un " + tipoZapato);
    }

    trabajar(Trabajador t) {
        System.out.println("No hago nada!!");
    }
}
```


**¿Qué pasa con Ruby?**

```ruby
class Taller
  def trabajar(artesano)
    puts "Hice un duendecito!"
  end

  def trabajar(zapatero, tipoZapato)
    puts "Hice un " + tipoZapato
  end

  def trabajar(trabajador)
    puts "No hago nada!!"
  end
end
```

> No tenemos forma de explicitar tipos!