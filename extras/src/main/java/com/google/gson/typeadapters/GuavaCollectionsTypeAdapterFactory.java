/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.typeadapters;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * {@link TypeAdapterFactory} for the most common {@code com.google.common.collect} classes from
 * Guava. This currently supports {@link ImmutableList}, {@link ImmutableSet}, {@link
 * ImmutableSortedSet}, {@link ImmutableMap}, {@link ImmutableSortedMap}, and {@link
 * ImmutableBiMap}.
 */
public class GuavaCollectionsTypeAdapterFactory implements TypeAdapterFactory {
  public static final GuavaCollectionsTypeAdapterFactory INSTANCE =
      new GuavaCollectionsTypeAdapterFactory();

  private static final ImmutableMap<Class<?>, Function<List<?>, Collection<?>>> COLLECTION_TYPES =
      ImmutableMap.of(
          ImmutableList.class,
          ImmutableList::copyOf,
          ImmutableSet.class,
          ImmutableSet::copyOf,
          ImmutableSortedSet.class,
          ImmutableSortedSet::copyOf);

  private static final ImmutableMap<Class<?>, Function<Map<?, ?>, Map<?, ?>>> MAP_TYPES =
      ImmutableMap.of(
          ImmutableMap.class,
          ImmutableMap::copyOf,
          ImmutableSortedMap.class,
          ImmutableSortedMap::copyOf,
          ImmutableBiMap.class,
          ImmutableBiMap::copyOf);

  private GuavaCollectionsTypeAdapterFactory() {}

  @Override
  @Nullable
  public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> typeToken) {
    Class<?> raw;
    if (typeToken.getType() instanceof Class<?>) {
      raw = (Class<?>) typeToken.getType();
    } else if (typeToken.getType() instanceof ParameterizedType) {
      raw = (Class<?>) ((ParameterizedType) typeToken.getType()).getRawType();
    } else {
      return null;
    }
    if (COLLECTION_TYPES.containsKey(raw)) {
      Type[] typeArguments = typeToken.getType() instanceof ParameterizedType
          ? ((ParameterizedType) typeToken.getType()).getActualTypeArguments()
          : new Type[] {Object.class};
      @SuppressWarnings("unchecked")
      TypeToken<List<?>> listType =
          (TypeToken<List<?>>) TypeToken.getParameterized(List.class, typeArguments);
      TypeAdapter<List<?>> listAdapter = requireNonNull(gson.getAdapter(listType));
      return new CollectionTypeAdapter<>(listAdapter, COLLECTION_TYPES.get(raw));
    } else if (MAP_TYPES.containsKey(raw)) {
      Type[] typeArguments =
          typeToken.getType() instanceof ParameterizedType
              ? ((ParameterizedType) typeToken.getType()).getActualTypeArguments()
              : new Type[] {Object.class, Object.class};
      @SuppressWarnings("unchecked")
      TypeToken<Map<?, ?>> mapType =
          (TypeToken<Map<?, ?>>) TypeToken.getParameterized(Map.class, typeArguments);
      TypeAdapter<Map<?, ?>> mapAdapter = requireNonNull(gson.getAdapter(mapType));
      return new MapTypeAdapter<>(mapAdapter, MAP_TYPES.get(raw));
    } else {
      return null;
    }
  }

  private static class CollectionTypeAdapter<T> extends TypeAdapter<T> {
    private final TypeAdapter<List<?>> listAdapter;
    private final Function<List<?>, Collection<?>> copyOfListFunction;

    CollectionTypeAdapter(
        TypeAdapter<List<?>> listAdapter, Function<List<?>, Collection<?>> copyOfListFunction) {
      this.listAdapter = listAdapter;
      this.copyOfListFunction = copyOfListFunction;
    }

    @Override
    public T read(JsonReader in) throws IOException {
      @SuppressWarnings("unchecked")
      T value = (T) copyOfListFunction.apply(listAdapter.read(in));
      return value;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
      listAdapter.write(out, (List<?>) value);
    }
  }

  private static class MapTypeAdapter<T> extends TypeAdapter<T> {
    private final TypeAdapter<Map<?, ?>> mapAdapter;
    private final Function<Map<?, ?>, Map<?, ?>> copyOfMapFunction;

    MapTypeAdapter(
        TypeAdapter<Map<?, ?>> mapAdapter, Function<Map<?, ?>, Map<?, ?>> copyOfMapFunction) {
      this.mapAdapter = mapAdapter;
      this.copyOfMapFunction = copyOfMapFunction;
    }

    @Override
    public T read(JsonReader in) throws IOException {
      @SuppressWarnings("unchecked")
      T value = (T) copyOfMapFunction.apply(mapAdapter.read(in));
      return value;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
      mapAdapter.write(out, (Map<?, ?>) value);
    }
  }
}
