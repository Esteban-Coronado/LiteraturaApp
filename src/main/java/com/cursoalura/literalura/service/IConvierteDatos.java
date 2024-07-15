package com.cursoalura.literalura.service;

public interface IConvierteDatos {
    <T> T getData(String json, Class<T> clase);
}
