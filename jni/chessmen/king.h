/*
created with obj2opengl.pl

source file    : ./king.obj
vertices       : 16
faces          : 24
normals        : 24
texture coords : 42


// include generated arrays
#import "./king.h"

// set input data to arrays
glVertexPointer(3, GL_FLOAT, 0, kingVertices);
glNormalPointer(GL_FLOAT, 0, kingNormals);
glTexCoordPointer(2, GL_FLOAT, 0, kingTexCoords);

// draw data
glDrawArrays(GL_TRIANGLES, 0, kingNumVertices);
*/

unsigned int kingNumVertices = 72;

float kingVertices [] = {
  // f 16/1/1 1/2/1 6/3/1
  0.299825505152732, 0.300150319619639, 0.0493603874791535,
  -0.300164606928298, 0.299753823858961, 0.0494783572054784,
  -0.29983468329534, -0.300223561197652, 0.0493559819707014,
  // f 7/4/2 10/5/2 1/6/2
  0.299963054916621, 0.300036265900827, -0.550299800968918,
  -0.299798949726785, 0.299907527153841, -0.550638535618781,
  -0.300164606928298, 0.299753823858961, 0.0494783572054784,
  // f 10/7/3 11/8/3 6/9/3
  -0.299798949726785, 0.299907527153841, -0.550638535618781,
  -0.299980554575194, -0.299854477489565, -0.550540635430959,
  -0.29983468329534, -0.300223561197652, 0.0493559819707014,
  // f 11/10/4 8/11/4 15/12/4
  -0.299980554575194, -0.299854477489565, -0.550540635430959,
  0.300028158541522, -0.299976852724342, -0.550418749697121,
  0.300149065273482, -0.29982951294167, 0.0494808047101739,
  // f 8/13/5 7/14/5 16/15/5
  0.300028158541522, -0.299976852724342, -0.550418749697121,
  0.299963054916621, 0.300036265900827, -0.550299800968918,
  0.299825505152732, 0.300150319619639, 0.0493603874791535,
  // f 9/16/6 13/17/6 2/18/6
  -8.92727337698273e-05, 0.299525716421337, 0.0519473999423369,
  0.29949705002259, 1.6734813356095e-05, 0.0518152346887778,
  -0.000125006302324674, -0.299433506681932, 0.0516909014502445,
  // f 3/19/7 4/16/7 5/17/7
  -0.000274304088752477, 0.299508094387529, 0.449224404118269,
  -0.29952727670558, -0.000300951296124766, 0.449354121867133,
  0.000226455371954547, -0.299573993451456, 0.449226362122026,
  // f 9/20/8 12/21/8 4/22/8
  -8.92727337698273e-05, 0.299525716421337, 0.0519473999423369,
  -0.299372104907883, 7.74329298052204e-05, 0.0516023017802661,
  -0.29952727670558, -0.000300951296124766, 0.449354121867133,
  // f 12/23/9 2/24/9 5/25/9
  -0.299372104907883, 7.74329298052204e-05, 0.0516023017802661,
  -0.000125006302324674, -0.299433506681932, 0.0516909014502445,
  0.000226455371954547, -0.299573993451456, 0.449226362122026,
  // f 2/26/10 13/27/10 14/28/10
  -0.000125006302324674, -0.299433506681932, 0.0516909014502445,
  0.29949705002259, 1.6734813356095e-05, 0.0518152346887778,
  0.299477469985026, 0.000216940697450808, 0.449361464381219,
  // f 13/29/11 9/30/11 3/31/11
  0.29949705002259, 1.6734813356095e-05, 0.0518152346887778,
  -8.92727337698273e-05, 0.299525716421337, 0.0519473999423369,
  -0.000274304088752477, 0.299508094387529, 0.449224404118269,
  // f 7/2/12 8/3/12 11/32/12
  0.299963054916621, 0.300036265900827, -0.550299800968918,
  0.300028158541522, -0.299976852724342, -0.550418749697121,
  -0.299980554575194, -0.299854477489565, -0.550540635430959,
  // f 15/32/13 16/1/13 6/3/13
  0.300149065273482, -0.29982951294167, 0.0494808047101739,
  0.299825505152732, 0.300150319619639, 0.0493603874791535,
  -0.29983468329534, -0.300223561197652, 0.0493559819707014,
  // f 16/33/14 7/4/14 1/6/14
  0.299825505152732, 0.300150319619639, 0.0493603874791535,
  0.299963054916621, 0.300036265900827, -0.550299800968918,
  -0.300164606928298, 0.299753823858961, 0.0494783572054784,
  // f 1/34/15 10/7/15 6/9/15
  -0.300164606928298, 0.299753823858961, 0.0494783572054784,
  -0.299798949726785, 0.299907527153841, -0.550638535618781,
  -0.29983468329534, -0.300223561197652, 0.0493559819707014,
  // f 6/35/16 11/10/16 15/12/16
  -0.29983468329534, -0.300223561197652, 0.0493559819707014,
  -0.299980554575194, -0.299854477489565, -0.550540635430959,
  0.300149065273482, -0.29982951294167, 0.0494808047101739,
  // f 15/36/17 8/13/17 16/15/17
  0.300149065273482, -0.29982951294167, 0.0494808047101739,
  0.300028158541522, -0.299976852724342, -0.550418749697121,
  0.299825505152732, 0.300150319619639, 0.0493603874791535,
  // f 12/19/18 9/16/18 2/18/18
  -0.299372104907883, 7.74329298052204e-05, 0.0516023017802661,
  -8.92727337698273e-05, 0.299525716421337, 0.0519473999423369,
  -0.000125006302324674, -0.299433506681932, 0.0516909014502445,
  // f 14/37/19 3/19/19 5/17/19
  0.299477469985026, 0.000216940697450808, 0.449361464381219,
  -0.000274304088752477, 0.299508094387529, 0.449224404118269,
  0.000226455371954547, -0.299573993451456, 0.449226362122026,
  // f 3/38/20 9/20/20 4/22/20
  -0.000274304088752477, 0.299508094387529, 0.449224404118269,
  -8.92727337698273e-05, 0.299525716421337, 0.0519473999423369,
  -0.29952727670558, -0.000300951296124766, 0.449354121867133,
  // f 4/39/21 12/23/21 5/25/21
  -0.29952727670558, -0.000300951296124766, 0.449354121867133,
  -0.299372104907883, 7.74329298052204e-05, 0.0516023017802661,
  0.000226455371954547, -0.299573993451456, 0.449226362122026,
  // f 5/40/22 2/26/22 14/28/22
  0.000226455371954547, -0.299573993451456, 0.449226362122026,
  -0.000125006302324674, -0.299433506681932, 0.0516909014502445,
  0.299477469985026, 0.000216940697450808, 0.449361464381219,
  // f 14/41/23 13/29/23 3/31/23
  0.299477469985026, 0.000216940697450808, 0.449361464381219,
  0.29949705002259, 1.6734813356095e-05, 0.0518152346887778,
  -0.000274304088752477, 0.299508094387529, 0.449224404118269,
  // f 10/42/24 7/2/24 11/32/24
  -0.299798949726785, 0.299907527153841, -0.550638535618781,
  0.299963054916621, 0.300036265900827, -0.550299800968918,
  -0.299980554575194, -0.299854477489565, -0.550540635430959,
};

float kingNormals [] = {
  // f 16/1/1 1/2/1 6/3/1
  0.000196999992078138, -0.000203999991796651, 0.999999959787503,
  0.000196999992078138, -0.000203999991796651, 0.999999959787503,
  0.000196999992078138, -0.000203999991796651, 0.999999959787503,
  // f 7/4/2 10/5/2 1/6/2
  -0.000214999987930546, 0.999999943863005, 0.000256999985572792,
  -0.000214999987930546, 0.999999943863005, 0.000256999985572792,
  -0.000214999987930546, 0.999999943863005, 0.000256999985572792,
  // f 10/7/3 11/8/3 6/9/3
  -0.999999924873509, 0.0003019999773118, 0.000242999981744263,
  -0.999999924873509, 0.0003019999773118, 0.000242999981744263,
  -0.999999924873509, 0.0003019999773118, 0.000242999981744263,
  // f 11/10/4 8/11/4 15/12/4
  -0.000203999989532251, -0.999999948687504, 0.000246999987325813,
  -0.000203999989532251, -0.999999948687504, 0.000246999987325813,
  -0.000203999989532251, -0.999999948687504, 0.000246999987325813,
  // f 8/13/5 7/14/5 16/15/5
  0.999999967947501, 0.00010799999653833, 0.000228999992659978,
  0.999999967947501, 0.00010799999653833, 0.000228999992659978,
  0.999999967947501, 0.00010799999653833, 0.000228999992659978,
  // f 9/16/6 13/17/6 2/18/6
  -1.29999988082057e-05, 0.000427999960762463, -0.999999908323513,
  -1.29999988082057e-05, 0.000427999960762463, -0.999999908323513,
  -1.29999988082057e-05, 0.000427999960762463, -0.999999908323513,
  // f 3/19/7 4/16/7 5/17/7
  0.000429999960243066, 3.99999963016805e-06, 0.999999907542013,
  0.000429999960243066, 3.99999963016805e-06, 0.999999907542013,
  0.000429999960243066, 3.99999963016805e-06, 0.999999907542013,
  // f 9/20/8 12/21/8 4/22/8
  -0.707302198772811, 0.706911198662929, 0.000396000111287729,
  -0.707302198772811, 0.706911198662929, 0.000396000111287729,
  -0.707302198772811, 0.706911198662929, 0.000396000111287729,
  // f 12/23/9 2/24/9 5/25/9
  -0.707418162927618, -0.706795162784133, 0.000375000086367405,
  -0.707418162927618, -0.706795162784133, 0.000375000086367405,
  -0.707418162927618, -0.706795162784133, 0.000375000086367405,
  // f 2/26/10 13/27/10 14/28/10
  0.70690419808236, -0.707309198195845, 0.000391000109562547,
  0.70690419808236, -0.707309198195845, 0.000391000109562547,
  0.70690419808236, -0.707309198195845, 0.000391000109562547,
  // f 13/29/11 9/30/11 3/31/11
  0.707015729290446, 0.70719772922076, 0.000360999861776609,
  0.707015729290446, 0.70719772922076, 0.000360999861776609,
  0.707015729290446, 0.70719772922076, 0.000360999861776609,
  // f 7/2/12 8/3/12 11/32/12
  0.000202999991797786, 0.000198999991959406, -0.999999959595003,
  0.000202999991797786, 0.000198999991959406, -0.999999959595003,
  0.000202999991797786, 0.000198999991959406, -0.999999959595003,
  // f 15/32/13 16/1/13 6/3/13
  -0.000207999991298841, 0.000200999991591668, 0.999999958167503,
  -0.000207999991298841, 0.000200999991591668, 0.999999958167503,
  -0.000207999991298841, 0.000200999991591668, 0.999999958167503,
  // f 16/33/14 7/4/14 1/6/14
  -0.000660999843414113, 0.999999763107584, -0.000191999954516656,
  -0.000660999843414113, 0.999999763107584, -0.000191999954516656,
  -0.000660999843414113, 0.999999763107584, -0.000191999954516656,
  // f 1/34/15 10/7/15 6/9/15
  -0.999999663859169, -0.000548999815458684, -0.000608999795290234,
  -0.999999663859169, -0.000548999815458684, -0.000608999795290234,
  -0.999999663859169, -0.000548999815458684, -0.000608999795290234,
  // f 6/35/16 11/10/16 15/12/16
  0.00065699973355217, -0.999999594447747, -0.000615999750179812,
  0.00065699973355217, -0.999999594447747, -0.000615999750179812,
  0.00065699973355217, -0.999999594447747, -0.000615999750179812,
  // f 15/36/17 8/13/17 16/15/17
  0.999999834337541, 0.000538999910707935, -0.000201999966536183,
  0.999999834337541, 0.000538999910707935, -0.000201999966536183,
  0.999999834337541, 0.000538999910707935, -0.000201999966536183,
  // f 12/19/18 9/16/18 2/18/18
  0.000723999743935816, 0.00042799984862504, -0.999999646320188,
  0.000723999743935816, 0.00042799984862504, -0.999999646320188,
  0.000723999743935816, 0.00042799984862504, -0.999999646320188,
  // f 14/37/19 3/19/19 5/17/19
  -0.000453999953209632, 2.99999969081255e-06, 0.999999896937516,
  -0.000453999953209632, 2.99999969081255e-06, 0.999999896937516,
  -0.000453999953209632, 2.99999969081255e-06, 0.999999896937516,
  // f 3/38/20 9/20/20 4/22/20
  -0.707762944769721, 0.706449944872181, -0.000298999976667538,
  -0.707762944769721, 0.706449944872181, -0.000298999976667538,
  -0.707762944769721, 0.706449944872181, -0.000298999976667538,
  // f 4/39/21 12/23/21 5/25/21
  -0.706539235259588, -0.707673235637181, -0.000949000315992958,
  -0.706539235259588, -0.707673235637181, -0.000949000315992958,
  -0.706539235259588, -0.707673235637181, -0.000949000315992958,
  // f 5/40/22 2/26/22 14/28/22
  0.707744222873999, -0.706468222472177, -0.000875000275544193,
  0.707744222873999, -0.706468222472177, -0.000875000275544193,
  0.707744222873999, -0.706468222472177, -0.000875000275544193,
  // f 14/41/23 13/29/23 3/31/23
  0.706563035854542, 0.707650035909702, -0.000321000016289146,
  0.706563035854542, 0.707650035909702, -0.000321000016289146,
  0.706563035854542, 0.707650035909702, -0.000321000016289146,
  // f 10/42/24 7/2/24 11/32/24
  0.00056499990231322, -0.000162999971817796, -0.999999827103045,
  0.00056499990231322, -0.000162999971817796, -0.999999827103045,
  0.00056499990231322, -0.000162999971817796, -0.999999827103045,
};

float kingTexCoords [] = {
  // f 16/1/1 1/2/1 6/3/1
  0.006400, 0.9936,
  0.993600, 0.9936,
  0.993600, 0.00639999999999996,
  // f 7/4/2 10/5/2 1/6/2
  0.000762, 0.996957,
  0.993600, 0.989028,
  0.993600, 0.011014,
  // f 10/7/3 11/8/3 6/9/3
  0.007177, 0.986504,
  0.993600, 0.989246,
  0.993600, 0.00903600000000004,
  // f 11/10/4 8/11/4 15/12/4
  0.011001, 0.9936,
  0.993600, 0.989323,
  0.993600, 0.014731,
  // f 8/13/5 7/14/5 16/15/5
  0.007651, 0.991239,
  0.988955, 0.993538,
  0.989494, 0.00735399999999997,
  // f 9/16/6 13/17/6 2/18/6
  0.996100, 0.9961,
  0.996100, 0.00390000000000001,
  -0.000299, 0.00211899999999998,
  // f 3/19/7 4/16/7 5/17/7
  0.003900, 0.9961,
  0.996100, 0.9961,
  0.996100, 0.00390000000000001,
  // f 9/20/8 12/21/8 4/22/8
  0.002264, 1.003022,
  0.996496, 1.004032,
  0.996502, -0.00395600000000007,
  // f 12/23/9 2/24/9 5/25/9
  0.003497, 1.004177,
  0.996538, 1.004594,
  0.996165, 0.00263599999999997,
  // f 2/26/10 13/27/10 14/28/10
  0.003020, 0.999844,
  0.996561, 1.004982,
  0.996147, 0.00297199999999997,
  // f 13/29/11 9/30/11 3/31/11
  0.000022, 0.999913,
  0.998098, 1.003356,
  1.004005, 0.00390000000000001,
  // f 7/2/12 8/3/12 11/32/12
  0.993600, 0.9936,
  0.993600, 0.00639999999999996,
  0.006400, 0.00639999999999996,
  // f 15/32/13 16/1/13 6/3/13
  0.006400, 0.00639999999999996,
  0.006400, 0.9936,
  0.993600, 0.00639999999999996,
  // f 16/33/14 7/4/14 1/6/14
  0.006400, 0.014783,
  0.000762, 0.996957,
  0.993600, 0.011014,
  // f 1/34/15 10/7/15 6/9/15
  0.006855, 0.010667,
  0.007177, 0.986504,
  0.993600, 0.00903600000000004,
  // f 6/35/16 11/10/16 15/12/16
  0.010168, 0.00755300000000003,
  0.011001, 0.9936,
  0.993600, 0.014731,
  // f 15/36/17 8/13/17 16/15/17
  0.011330, 0.00639999999999996,
  0.007651, 0.991239,
  0.989494, 0.00735399999999997,
  // f 12/19/18 9/16/18 2/18/18
  0.003900, 0.9961,
  0.996100, 0.9961,
  -0.000299, 0.00211899999999998,
  // f 14/37/19 3/19/19 5/17/19
  0.003900, 0.00390000000000001,
  0.003900, 0.9961,
  0.996100, 0.00390000000000001,
  // f 3/38/20 9/20/20 4/22/20
  0.000362, -0.000394000000000005,
  0.002264, 1.003022,
  0.996502, -0.00395600000000007,
  // f 4/39/21 12/23/21 5/25/21
  -0.001030, 0.000566999999999984,
  0.003497, 1.004177,
  0.996165, 0.00263599999999997,
  // f 5/40/22 2/26/22 14/28/22
  -0.007959, 0.00390000000000001,
  0.003020, 0.999844,
  0.996147, 0.00297199999999997,
  // f 14/41/23 13/29/23 3/31/23
  0.003472, -0.00456300000000009,
  0.000022, 0.999913,
  1.004005, 0.00390000000000001,
  // f 10/42/24 7/2/24 11/32/24
  0.000418, 0.998274,
  0.993600, 0.9936,
  0.006400, 0.00639999999999996,
};

