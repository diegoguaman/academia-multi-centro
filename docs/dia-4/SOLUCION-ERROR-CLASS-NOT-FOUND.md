# 🔧 Solución: Error ClassNotFoundException - MatriculaResponseDto

## ❌ El Problema

Al iniciar la aplicación, recibes este error:

```
Caused by: java.lang.ClassNotFoundException: MatriculaResponseDto
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
```

El error indica que Spring no puede encontrar la clase `MatriculaResponseDto`, aunque:
- ✅ La clase existe en `src/main/java/com/academy/academymanager/dto/response/MatriculaResponseDto.java`
- ✅ El import está correcto en `MatriculaResolver`
- ✅ La compilación es exitosa

---

## 🔍 Causa Raíz

Este error es causado por **Spring DevTools** y su ClassLoader. Cuando Spring DevTools intenta recargar clases durante el desarrollo, a veces no puede encontrar clases que fueron modificadas o que tienen dependencias complejas.

**El problema específico:**

El método `@BatchMapping` en `MatriculaResolver` está usando `MatriculaResponseDto` como tipo, y Spring DevTools tiene problemas para cargar esta clase durante la inicialización del contexto.

---

## ✅ Solución Aplicada

**Temporalmente** hemos comentado el método `@BatchMapping` problemático:

```java
// @BatchMapping
// public Map<MatriculaResponseDto, List<Object>> calificaciones(
//         final List<MatriculaResponseDto> matriculas
// ) {
//     return java.util.Collections.emptyMap();
// }
```

---

## 🚀 Pasos para Solucionar

### Opción 1: Reiniciar Completamente (Recomendado)

1. **Detén la aplicación completamente** (no solo recargar)
2. **Limpia el proyecto:**
   ```bash
   mvn clean
   ```
3. **Recompila:**
   ```bash
   mvn compile
   ```
4. **Reinicia la aplicación** desde cero

### Opción 2: Deshabilitar Spring DevTools Temporalmente

Si el problema persiste, puedes deshabilitar Spring DevTools temporalmente:

**En `pom.xml`, comenta la dependencia:**
```xml
<!--
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
-->
```

Luego recompila y reinicia.

### Opción 3: Usar el Método BatchMapping Correctamente

Cuando implementes el BatchMapping, asegúrate de usar los tipos correctos:

```java
@BatchMapping
public Map<MatriculaResponseDto, List<CalificacionResponseDto>> calificaciones(
        final List<MatriculaResponseDto> matriculas
) {
    // Extraer IDs de matrículas
    List<Long> matriculaIds = matriculas.stream()
            .map(MatriculaResponseDto::getIdMatricula)
            .distinct()
            .collect(Collectors.toList());
    
    // 1 sola query para todas las calificaciones
    Map<Long, List<CalificacionResponseDto>> calificacionesMap = 
            calificacionRepository
                .findByMatriculaIdMatriculaIn(matriculaIds)
                .stream()
                .collect(Collectors.groupingBy(
                    c -> c.getMatricula().getIdMatricula(),
                    Collectors.mapping(
                        calificacionMapper::toResponseDto,
                        Collectors.toList()
                    )
                ));
    
    // Mapear cada matrícula a sus calificaciones
    return matriculas.stream()
            .collect(Collectors.toMap(
                matricula -> matricula,
                matricula -> calificacionesMap.getOrDefault(
                    matricula.getIdMatricula(),
                    Collections.emptyList()
                )
            ));
}
```

---

## 🔍 Verificación

Después de aplicar la solución, verifica:

1. ✅ La aplicación inicia sin errores
2. ✅ Puedes acceder a GraphiQL
3. ✅ Las queries básicas funcionan

---

## 📝 Notas Adicionales

### ¿Por Qué Pasa Esto con Spring DevTools?

Spring DevTools usa un **ClassLoader especial** para recargar clases automáticamente durante el desarrollo. A veces:

1. **Dependencias circulares** entre clases pueden causar problemas
2. **Clases que se cargan muy temprano** en el ciclo de vida pueden no estar disponibles
3. **Anotaciones complejas** como `@BatchMapping` pueden causar problemas de inicialización

### Solución Permanente

Cuando implementes el BatchMapping completo:

1. **Asegúrate de que todas las clases relacionadas estén compiladas**
2. **Usa tipos concretos** en lugar de `List<Object>`
3. **Implementa la lógica completa** en lugar de retornar `emptyMap()`

---

## ✅ Estado Actual

- ✅ Método `@BatchMapping` comentado temporalmente
- ✅ Aplicación debería iniciar sin errores
- ⚠️ BatchMapping para calificaciones pendiente de implementar

---

## 🎯 Próximos Pasos

1. ✅ Reinicia la aplicación y verifica que funciona
2. 🔄 (Opcional) Deshabilita Spring DevTools si el problema persiste
3. 🔄 (Futuro) Implementa el BatchMapping correctamente cuando crees `CalificacionResponseDto`

---

**Última actualización:** Día 4 - Solución Error ClassNotFoundException  
**Estado:** ✅ Solucionado temporalmente

