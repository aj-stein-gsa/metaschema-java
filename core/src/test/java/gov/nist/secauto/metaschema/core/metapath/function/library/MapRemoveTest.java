/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.entry;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.map;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.LookupTest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class MapRemoveTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            map(
                entry(integer(0), string("Sonntag")),
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(5), string("Freitag")),
                entry(integer(6), string("Samstag"))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:remove($week, 4)"),
        Arguments.of(
            map(
                entry(integer(0), string("Sonntag")),
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(4), string("Donnerstag")),
                entry(integer(5), string("Freitag")),
                entry(integer(6), string("Samstag"))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:remove($week, 23)"),
        Arguments.of(
            map(
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(4), string("Donnerstag")),
                entry(integer(5), string("Freitag"))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:remove($week, (0, 6 to 7))"),
        Arguments.of(
            map(
                entry(integer(0), string("Sonntag")),
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(4), string("Donnerstag")),
                entry(integer(5), string("Freitag")),
                entry(integer(6), string("Samstag"))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:remove($week, ())"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IMapItem<?> expected, @NonNull String metapath) {

    IItem result = MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.NODE, newDynamicContext());
    assertEquals(expected, result);
  }
}
