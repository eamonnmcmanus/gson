package com.google.gson.typeadapters;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class GuavaCollectionsTypeAdapterFactoryTest {
  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapterFactory(GuavaCollectionsTypeAdapterFactory.INSTANCE)
          .create();

  @Test
  public void immutableList_object() {
    ImmutableList<Object> list = ImmutableList.of("foo", 23.0, ImmutableList.of("bar", "baz"));
    String json = GSON.toJson(list);
    assertThat(json).isEqualTo("[\"foo\",23.0,[\"bar\",\"baz\"]]");
    ImmutableList<Object> result = GSON.fromJson(json, new TypeToken<ImmutableList<Object>>() {});
    assertThat(result).isEqualTo(list);
  }

  @Test
  public void immutableList_nestedList() {
    ImmutableList<ImmutableList<String>> list =
        ImmutableList.of(ImmutableList.of(), ImmutableList.of("foo", "bar"));
    String json = GSON.toJson(list);
    assertThat(json).isEqualTo("[[],[\"foo\",\"bar\"]]");
    ImmutableList<ImmutableList<String>> result =
        GSON.fromJson(json, new TypeToken<ImmutableList<ImmutableList<String>>>() {});
    assertThat(result).isEqualTo(list);
  }

  @Test
  public void immutableList_raw() {
    ImmutableList<String> list = ImmutableList.of("foo", "bar");
    String json = GSON.toJson(list);
    assertThat(json).isEqualTo("[\"foo\",\"bar\"]");
    @SuppressWarnings("unchecked")
    ImmutableList<String> result = GSON.fromJson(json, ImmutableList.class);
    assertThat(result).isEqualTo(list);
  }

  @Test
  public void immutableSet_object() {
    ImmutableSet<Object> set = ImmutableSet.of("foo", 23.0, ImmutableList.of("bar", "baz"));
    String json = GSON.toJson(set);
    assertThat(json).isEqualTo("[\"foo\",23.0,[\"bar\",\"baz\"]]");
    ImmutableSet<Object> result = GSON.fromJson(json, new TypeToken<ImmutableSet<Object>>() {});
    assertThat(result).isEqualTo(set);
  }

  @Test
  public void immutableSortedSet_string() {
    ImmutableSortedSet<String> set = ImmutableSortedSet.of("foo", "bar", "baz");
    String json = GSON.toJson(set);
    assertThat(json).isEqualTo("[\"bar\",\"baz\",\"foo\"]");
    ImmutableSortedSet<String> result =
        GSON.fromJson(json, new TypeToken<ImmutableSortedSet<String>>() {});
    assertThat(result).isEqualTo(set);
  }

  @Test
  public void immutableMap_string() {
    ImmutableMap<String, String> map = ImmutableMap.of("foo", "bar", "baz", "buh");
    String json = GSON.toJson(map);
    assertThat(json).isEqualTo("{\"foo\":\"bar\",\"baz\":\"buh\"}");
    ImmutableMap<String, String> result =
        GSON.fromJson(json, new TypeToken<ImmutableMap<String, String>>() {});
    assertThat(result).isEqualTo(map);
  }

  @Test
  public void immutableSortedMap_string() {
    ImmutableSortedMap<String, String> map = ImmutableSortedMap.of("foo", "bar", "baz", "buh");
    String json = GSON.toJson(map);
    assertThat(json).isEqualTo("{\"baz\":\"buh\",\"foo\":\"bar\"}");
    ImmutableSortedMap<String, String> result =
        GSON.fromJson(json, new TypeToken<ImmutableSortedMap<String, String>>() {});
    assertThat(result).isEqualTo(map);
  }

  @Test
  public void immutableBiMap_string() {
    ImmutableBiMap<String, String> map = ImmutableBiMap.of("foo", "bar", "baz", "buh");
    String json = GSON.toJson(map);
    assertThat(json).isEqualTo("{\"foo\":\"bar\",\"baz\":\"buh\"}");
    ImmutableBiMap<String, String> result =
        GSON.fromJson(json, new TypeToken<ImmutableBiMap<String, String>>() {});
    assertThat(result).isEqualTo(map);
  }
}
