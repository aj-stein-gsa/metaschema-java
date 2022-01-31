/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.model.common.datatype.adapter;

import gov.nist.secauto.metaschema.model.common.datatype.AbstractJavaTypeAdapter;
import gov.nist.secauto.metaschema.model.common.metapath.type.IIPv6AddressType;

import org.jetbrains.annotations.NotNull;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.IPAddressStringParameters;
import inet.ipaddr.IncompatibleAddressException;
import inet.ipaddr.ipv6.IPv6Address;

public class IPv6AddressAdapter
    extends AbstractJavaTypeAdapter<IPv6Address, IIPv6AddressItem>
    implements IIPv6AddressType {
  private static final IPAddressStringParameters IPv6;

  static {
    IPv6 = new IPAddressStringParameters.Builder().allowIPv4(false).allowEmpty(false).allowSingleSegment(false)
        .allowWildcardedSeparator(false).getIPv6AddressParametersBuilder().allowBinary(false).allowLeadingZeros(false)
        .allowPrefixesBeyondAddressSize(false).getParentBuilder().toParams();
  }

  @SuppressWarnings("null")
  public IPv6AddressAdapter() {
    super(IPv6Address.class);
  }

  @Override
  public String getName() {
    return "ip-v6-address";
  }

  @SuppressWarnings("null")
  @Override
  public IPv6Address parse(String value) throws IllegalArgumentException {
    try {
      return (IPv6Address) new IPAddressString(value, IPv6).toAddress();
    } catch (AddressStringException | IncompatibleAddressException ex) {
      throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
    }
  }

  @Override
  public IPv6Address copy(Object obj) {
    // value is immutable
    return (IPv6Address) obj;
  }

  @SuppressWarnings("null")
  @Override
  public @NotNull Class<IIPv6AddressItem> getItemClass() {
    return IIPv6AddressItem.class;
  }

  @Override
  public IIPv6AddressItem newItem(Object value) {
    IPv6Address item = toValue(value);
    return new IPv6AddressItemImpl(item);
  }
}
