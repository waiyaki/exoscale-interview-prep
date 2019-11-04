# asn1 parser
A simple ASN.1 parser that handles private keys in the EC and RSA format.

## Installation

Clone this repository and change directory into `asn1`

## Usage

The parser can be invoked using `lein`:

```bash
$ lein run <path-to-key>
```

It outputs a data structure containing the information encoded in the key.

## Examples

Consider a file `key.pem` with the contents:

``` text
-----BEGIN EC PRIVATE KEY-----
MHQCAQEEIOl4j5ur73lcB1E640cN7ZlitMON4+ccN4Im97MM6JM+oAcGBSuBBAAK
oUQDQgAEbNW0viHDh4+dVQbiWBGYTy4UqJpjpHYjfFb5V9kRxCCL9ufkXMif58H8
4XpMA3P1zhrFjEljMjjYYAj6MuBMpA==
-----END EC PRIVATE KEY-----
```

Invoking the parser as `lein run key.pem` yields:

```edn
{:tag-name :sequence,
 :tag-type "0x30",
 :length 116,
 :length-size 0,
 :children
 [{:tag-name :integer,
   :tag-type "0x2",
   :length 1,
   :length-size 0,
   :value 1}
  {:tag-name :octet-string,
   :tag-type "0x4",
   :length 32,
   :length-size 0,
   :value
   "E9788F9BABEF795C07513AE3470DED9962B4C38DE3E71C378226F7B30CE8933E"}
  {:tag-name "0xA0",
   :tag-type "0xA0",
   :length 7,
   :length-size 0,
   :children
   [{:tag-name :object-identifier,
     :tag-type "0x6",
     :length 5,
     :length-size 0,
     :value "2B8104000A"}]}
  {:tag-name "0xA1",
   :tag-type "0xA1",
   :length 68,
   :length-size 0,
   :children
   [{:tag-name :bit-string,
     :tag-type "0x3",
     :length 66,
     :length-size 0,
     :value
     "10001101100110101011011010010111110001000011100001110000111100011111001110101010101000001101110001001011000000100011001100001001111001011100001010010101000100110100110001110100100011101100010001101111100010101101111100101010111110110010001000111000100001000001000101111110110111001111110010001011100110010001001111111100111110000011111110011100001011110100100110000000011011100111111010111001110000110101100010110001100010010010110001100110010001110001101100001100000000010001111101000110010111000000100110010100100"}]}]}
```

### Bugs

The current version of this parser does not decode object identifiers.

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
