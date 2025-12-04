package com.academy.academymanager.usertype;

import com.academy.academymanager.domain.entity.Usuario;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Custom Hibernate UserType for mapping a Java Enum (Usuario.Rol) to a 
 * PostgreSQL native ENUM type (rol_usuario).
 * 
 * This class handles the necessary casting required by the PostgreSQL JDBC driver.
 * 
 * Usage in entity:
 * @Column(name = "rol", nullable = false, columnDefinition = "rol_usuario")
 * @Type(PostgreSQLEnumType.class)
 * private Rol rol;
 */
public class PostgreSQLEnumType implements UserType<Usuario.Rol> {
    private static final Class<Usuario.Rol> ENUM_CLASS = Usuario.Rol.class;
    private static final String ENUM_TYPE_NAME = "rol_usuario";
    @Override
    public int getSqlType() {
        return Types.OTHER;
    }
    @Override
    public Class<Usuario.Rol> returnedClass() {
        return ENUM_CLASS;
    }
    @Override
    public boolean equals(final Usuario.Rol x, final Usuario.Rol y) throws HibernateException {
        return Objects.equals(x, y);
    }
    @Override
    public int hashCode(final Usuario.Rol x) throws HibernateException {
        return x != null ? x.hashCode() : 0;
    }
    @Override
    public Usuario.Rol nullSafeGet(
            final ResultSet rs,
            final int position,
            final SharedSessionContractImplementor session,
            final Object owner
    ) throws HibernateException, SQLException {
        final String enumName = rs.getString(position);
        if (rs.wasNull() || enumName == null) {
            return null;
        }
        try {
            return Usuario.Rol.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            throw new HibernateException("Unknown rol value: " + enumName, e);
        }
    }
    @Override
    public void nullSafeSet(
            final PreparedStatement st,
            final Usuario.Rol value,
            final int index,
            final SharedSessionContractImplementor session
    ) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        try {
            final Object pgObject = Class.forName("org.postgresql.util.PGobject")
                    .getDeclaredConstructor()
                    .newInstance();
            pgObject.getClass().getMethod("setType", String.class).invoke(pgObject, ENUM_TYPE_NAME);
            pgObject.getClass().getMethod("setValue", String.class).invoke(pgObject, value.name());
            st.setObject(index, pgObject, Types.OTHER);
        } catch (Exception e) {
            throw new HibernateException("Failed to create PGobject for enum: " + value, e);
        }
    }
    @Override
    public Usuario.Rol deepCopy(final Usuario.Rol value) throws HibernateException {
        return value;
    }
    @Override
    public boolean isMutable() {
        return false;
    }
    @Override
    public Serializable disassemble(final Usuario.Rol value) throws HibernateException {
        return value != null ? value.name() : null;
    }
    @Override
    public Usuario.Rol assemble(final Serializable cached, final Object owner) throws HibernateException {
        if (cached == null) {
            return null;
        }
        try {
            return Usuario.Rol.valueOf((String) cached);
        } catch (IllegalArgumentException e) {
            throw new HibernateException("Unknown rol value: " + cached, e);
        }
    }
    @Override
    public Usuario.Rol replace(final Usuario.Rol original, final Usuario.Rol target, final Object owner) throws HibernateException {
        return original;
    }
}

