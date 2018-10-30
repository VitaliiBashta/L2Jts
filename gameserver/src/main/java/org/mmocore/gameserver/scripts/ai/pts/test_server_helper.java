package org.mmocore.gameserver.scripts.ai.pts;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.model.buylist.Product;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.SiegeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author KilRoy
 * Сильно мнительным, сюда лучше не смотреть! АИ с скриптов ПТСа фреи. 31756, 31757
 */
public class test_server_helper extends DefaultAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(test_server_helper.class);
    private static final String fnYouAreThirdClass = "pts/testserverhelper/test_server_helper010.htm";
    private static final String fnYouAreFourthClass = "pts/testserverhelper/test_server_helper011.htm";
    private static final String fnHumanFighter = "pts/testserverhelper/test_server_helper012.htm";
    private static final String fnHumanMage = "pts/testserverhelper/test_server_helper013.htm";
    private static final String fnElfFighter = "pts/testserverhelper/test_server_helper014.htm";
    private static final String fnElfMage = "pts/testserverhelper/test_server_helper015.htm";
    private static final String fnDElfFighter = "pts/testserverhelper/test_server_helper016.htm";
    private static final String fnDElfMage = "pts/testserverhelper/test_server_helper017.htm";
    private static final String fnOrcFighter = "pts/testserverhelper/test_server_helper018.htm";
    private static final String fnOrcMage = "pts/testserverhelper/test_server_helper019.htm";
    private static final String fnDwarfFighter = "pts/testserverhelper/test_server_helper020.htm";
    private static final String fnAfterClassChange = "pts/testserverhelper/test_server_helper021.htm";
    private static final String fnPledgeLevelUP = "pts/testserverhelper/test_server_helper022.htm";
    private static final String fnLowLevel = "pts/testserverhelper/test_server_helper023.htm";
    private static final boolean CUSTOM_BUFF = true;
    private static final int potion_of_experience_silien_elder = 15309;
    private static final int potion_of_experience_elder = 15308;
    private static final int potion_of_experience_bishop = 15307;
    private static boolean locked = false;
    private static final int[] sellItemList0_ids = {1463, 1464, 1465, 1466, 1467, 2510, 2511, 2512, 2513, 2514, 3947, 3948, 3949, 3950, 3951, 3952, 5134, 5135, 5136, 5137, 5138, 5139, 5140, 5141, 5142, 5143, 5144, 5145, 5146, 5147, 5148, 5149, 5150, 5151, 5250, 5251, 5252, 5253, 5254, 5255, 5256, 5257, 5258, 5259, 5260, 5261, 5262, 5263, 5264, 5265, 5266, 5267, 1458, 1459, 1460, 1461, 1462, 2130, 2131, 2132, 2133, 2134, 5192, 5193, 5194, 5195, 5196, 1341, 1342, 1343, 1344, 1345, 734, 735, 1060, 1061, 1374, 1375, 1539, 6035, 6036, 5126, 5589, 1864, 1865, 1866, 1867, 1868, 1869, 1870, 1871, 1872, 1873, 1874, 1875, 1876, 1877, 4039, 4040, 4041, 4042, 4043, 4044, 1878, 1879, 1880, 1881, 1882, 5220, 5549, 1883, 1884, 1885, 1886, 1887, 1888, 1889, 5550, 1890, 1891, 1892, 1893, 1894, 1895, 4045, 4046, 5551, 4047, 4048, 5552, 5553, 5554, 6535, 6536, 6537, 6538, 6539, 6540, 9633, 9634, 9635, 9636, 9637, 9628, 9629, 9630, 729, 730, 947, 948, 951, 952, 955, 956, 959, 960, 8184, 8185, 8186, 8187, 8188, 8189, 8560, 8561, 8562, 8563, 8564, 8565};
    private static final int[] sellItemList1_ids = {1788, 1786, 1787, 2135, 1791, 1789, 2138, 2136, 1790, 1814, 1797, 1795, 1796, 1802, 2137, 1666, 1794, 1799, 1798, 1803, 1792, 1793, 1800, 1801, 2150, 2175, 2254, 2174, 2173, 2252, 2253, 2144, 1817, 2140, 2141, 2139, 2143, 2142};
    private static final int[] sellItemList2_ids = {8294, 8295, 8301, 8302, 8307, 8317, 2256, 2177, 2179, 2176, 2151, 2255, 2258, 2257, 5278, 5268, 5273, 3953, 5163, 5153, 5158, 1804, 3032, 2261, 2178, 2263, 2262, 2152, 2260, 2182, 2180, 2259, 2181, 5437, 2250, 2267, 2185, 2266, 2184, 2183, 2265, 2153, 2268, 2264, 2277, 2193, 2186, 2977, 2972, 2980, 2975, 2982, 2280, 2282, 2154, 2978, 2981, 2156, 2155, 2191, 2270, 2279, 2275, 2192, 2276, 2976, 2188, 2979, 2187, 2190, 2971, 2189, 2269, 2274, 2278, 2272, 2273, 2271, 2202, 2287, 2296, 2286, 2285, 2292, 3020, 2288, 2293, 2195, 2289, 2290, 2194, 2158, 2159, 2208, 2157, 2985, 2196, 2983, 2987, 2198, 2986, 2291, 2984, 2197, 2201, 2146, 2147, 2148, 5472, 2149, 2145, 1825, 5231, 5473};
    private static final int[] sellItemList3_ids = {8296, 8303, 8304, 8308, 8318, 8319, 2162, 2161, 2301, 2299, 2314, 2994, 2990, 2205, 2996, 2989, 2204, 2998, 2311, 2305, 2312, 2308, 2310, 2993, 2209, 2297, 2307, 2304, 2991, 2997, 2199, 2207, 2203, 2160, 2313, 2992, 2995, 2300, 2303, 2306, 2298, 2988, 2206, 2302, 5279, 5269, 5274, 3954, 5164, 5154, 5159, 3000, 2211, 2212, 2999, 1805, 3033, 2317, 2219, 2320, 2322, 2214, 3002, 2213, 3021, 2163, 2323, 3003, 2216, 2326, 2316, 2217, 4440, 2165, 2324, 2319, 3001, 2221, 3004, 2220, 2164, 2215, 2315, 2318, 2321, 2251, 2347, 2327, 2342, 2222, 3009, 3010, 4132, 2345, 2334, 3005, 3006, 2225, 2224, 2166, 2346, 2344, 2348, 2330, 2343, 2226, 2168, 2336, 2340, 2338, 2167, 2341, 2228, 2337, 2328, 2333, 2223, 2329, 2335, 2350, 2351, 3007, 2230, 3008, 2229, 2352, 3017, 4124, 2357, 2354, 2356, 2234, 2233, 3013, 3015, 2232, 2359, 2231, 3014, 3016, 3012, 3019, 2355, 2358, 4123, 4122, 4125, 2170, 2169, 2360, 5474, 2970, 2353, 3022, 5436};
    private static final int[] sellItemList4_ids = {8297, 8298, 8305, 8306, 8309, 8310, 8311, 8312, 8321, 8322, 8323, 8324, 4126, 4937, 4128, 4939, 4127, 4938, 4189, 4970, 4175, 4959, 4141, 4944, 4149, 4952, 4142, 4945, 4150, 4953, 4145, 4948, 4148, 4951, 4441, 4936, 4190, 4971, 4191, 4972, 4182, 4963, 4183, 4964, 4188, 4969, 4147, 4950, 4187, 4968, 4185, 4966, 4174, 4958, 4133, 4940, 4134, 4941, 4177, 4960, 4179, 4962, 4144, 4947, 4143, 4946, 4178, 4961, 4184, 4965, 4186, 4967, 4146, 4949, 5280, 5270, 5275, 3955, 5165, 5155, 5160, 4180, 1806, 3034, 4195, 5003, 4198, 5006, 4167, 4992, 4155, 4981, 4157, 4982, 4173, 4998, 4165, 4990, 4163, 4988, 4159, 4984, 4161, 4986, 4194, 5002, 4197, 5005, 4176, 4999, 4168, 4993, 4166, 4991, 4158, 4983, 4154, 4980, 4129, 4973, 4199, 5007, 4164, 4989, 4193, 5001, 4160, 4985, 4131, 4975, 4130, 4974, 4196, 5004, 4192, 5000, 4162, 4987};
    private static final int[] sellItemList5_ids = {8299, 8300, 8313, 8314, 8315, 8316, 8320, 8487, 8325, 8326, 5452, 5453, 5446, 5447, 5444, 5445, 5460, 5461, 5468, 5469, 5458, 5459, 5438, 5439, 5230, 5368, 5369, 5416, 5417, 5424, 5425, 5392, 5393, 5426, 5427, 5332, 5333, 5348, 5349, 5364, 5365, 5340, 5341, 6331, 6332, 6329, 6330, 6333, 6334, 5428, 5429, 5370, 5371, 5394, 5395, 5354, 5355, 5334, 5335, 5418, 5419, 5346, 5347, 5470, 5471, 5476, 5477, 5475, 5281, 5271, 5276, 3956, 5166, 5156, 5161, 1807, 3035, 5464, 5465, 5450, 5451, 5434, 5435, 5440, 5441, 5420, 5421, 5380, 5381, 5404, 5405, 5430, 5431, 5336, 5337, 5382, 5383, 5432, 5433, 5406, 5407, 6337, 6338, 5338, 5339, 6335, 6336, 5422, 5423, 6339, 6340, 5352, 5353, 5350, 5351, 5366, 5367, 5442, 5443, 5448, 5449, 5466, 5467, 5456, 5457, 5462, 5463, 8690, 8691, 8692, 8693, 8694, 8695, 8696, 8697, 8698, 8699, 8700, 8701, 8702, 8703, 8704, 8705, 8706, 8707, 8708, 8709, 8710, 8711};
    private static final int[] sellItemList6_ids = {6901, 5282, 5272, 5277, 3957, 5167, 5157, 5162, 1808, 3036, 6887, 6888, 6899, 6900, 6883, 6884, 6895, 6896, 6891, 6892, 6881, 6882, 6897, 6898, 6885, 6886, 6893, 6894, 7580, 7581, 6865, 6866, 6869, 6870, 6867, 6868, 6871, 6872, 6851, 6852, 6853, 6854, 6859, 6860, 6855, 6856, 6857, 6858, 6863, 6864, 6861, 6862, 13100, 6877, 6878, 6875, 6876, 6879, 6880, 6873, 6874, 6849, 6850, 6847, 6848, 9967, 9968, 9969, 9970, 9971, 9972, 9973, 9974, 9975, 10544, 10545, 9482, 9483, 9484, 9485, 9486, 9487, 9488, 9489, 9490, 9491, 9492, 9493, 9494, 9495, 9496, 9497, 10115, 9985, 9986, 9987, 10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381, 15775, 15776, 15777, 15778, 15779, 15780, 15781, 15782, 15783, 15784, 15785, 15786, 15787, 15788, 15789, 15790, 15791, 15792, 15793, 15794, 15795, 15796, 15797, 15798, 15799, 15800, 15801, 15802, 15803, 15804, 15805, 15806, 15807, 15808, 15809, 15810, 15811, 15812, 15813, 15814, 15815, 15816, 15817, 15818, 15819, 15820, 15821, 15822, 15823, 15824, 15825};
    private static final int[] sellItemList7_ids = {6926, 6929, 6931, 6937, 6933, 6934, 6920, 6930, 6932, 6927, 6939, 6921, 6943, 6944, 6945, 6946, 6947, 6948, 6949, 6950, 6951, 6952, 6953, 6940, 6941, 6942, 6935, 6936, 6938, 6957, 6956, 6959, 6958, 6960, 6961, 6962, 6963, 6954, 6955, 6964, 6965, 6969, 6968, 6971, 6970, 6972, 6973, 6974, 6975, 6966, 6967, 6976, 6977, 6924, 6923, 6925, 6922, 6981, 6980, 6983, 6982, 6984, 6985, 6986, 6987, 6978, 6979, 6988, 6989, 6928, 6993, 6992, 6995, 6994, 6996, 6997, 6998, 6999, 6990, 6991, 7000, 7001, 7005, 7004, 7007, 7006, 7008, 7009, 7010, 7011, 7002, 7003, 7012, 7013, 7693, 7700, 7699, 7691, 7690, 7692, 7689, 16110, 16111, 16112, 16113, 16114, 16115, 16116, 16117, 16118, 16119, 16120, 16121};
    private static final int[] sellItemList8_ids = {8327, 8328, 8332, 8333, 8337, 8343, 2007, 2005, 2006, 2010, 2008, 2009, 1923, 1921, 1922, 1896, 2013, 1925, 1924, 1897, 2011, 2012, 1926, 1928, 1898, 1930, 2016, 1929, 1927, 2014, 2015, 2018, 1932, 1934, 5531, 1931, 1899, 2017, 2020, 2019, 2023, 1933, 2025, 2024, 1900, 2022, 1937, 1935, 2021, 1936, 2029, 1940, 2028, 1939, 1938, 2027, 1901, 2030, 2026, 2039, 1948, 2924, 2919, 2927, 2922, 2929, 1941, 2042, 2044, 1902, 2925, 2928, 1904, 1903, 1946, 2032, 2041, 2037, 1947, 2038, 2923, 1943, 2926, 1942, 1945, 2918, 1944, 2031, 2036, 2040, 2034, 2035, 2033, 1957, 2049, 2058, 2048, 2047, 2054, 2967, 2050, 2055, 1950, 2051, 2052, 1949, 1906, 1907, 1963, 1905, 2932, 1951, 2930, 2934, 1953, 2933, 2053, 2931, 1952, 1956, 8073};
    private static final int[] sellItemList9_ids = {8329, 8334, 8335, 8338, 8344, 8345, 1910, 1909, 2063, 2061, 2076, 2941, 2937, 1960, 2943, 2936, 1959, 2945, 2073, 2067, 2074, 2070, 2072, 2940, 1964, 2059, 2069, 2066, 2938, 2944, 1954, 1962, 1958, 1908, 2075, 2939, 2942, 2062, 2065, 2068, 2060, 2935, 1961, 2064, 2947, 1966, 1967, 2946, 2079, 1974, 2082, 2084, 1969, 2949, 1968, 2968, 1911, 2085, 2950, 1971, 2088, 2078, 1972, 4438, 1913, 2086, 2081, 2948, 1976, 2951, 1975, 1912, 1970, 2077, 2080, 2083, 2109, 2089, 2104, 1977, 2956, 2957, 4055, 2107, 2096, 2952, 2953, 1980, 1979, 1914, 2108, 2106, 2110, 2092, 2105, 1981, 1916, 2098, 2102, 2100, 1915, 2103, 1983, 2099, 2090, 2095, 1978, 2091, 2097, 2112, 2113, 2954, 1985, 2955, 1984, 2114, 5530, 2964, 2119, 2116, 2118, 1989, 1988, 2960, 2962, 1987, 2121, 1986, 2961, 2963, 2959, 2966, 2117, 2120, 1918, 1920, 2122, 2917, 2115, 2969};
    private static final int[] sellItemList10_ids = {8330, 8336, 8339, 8340, 8347, 8348, 4049, 4051, 4050, 4111, 4098, 4064, 4072, 4065, 4073, 4068, 4071, 4439, 4112, 4113, 4104, 4105, 4110, 4070, 4109, 4107, 4097, 4056, 4057, 4100, 4102, 4067, 4066, 4101, 4106, 4108, 4069, 4117, 4120, 4090, 4078, 4080, 4096, 4088, 4086, 4082, 4084, 4116, 4119, 4099, 4091, 4089, 4081, 4077, 4052, 4121, 4087, 4115, 4083, 4054, 4053, 4118, 4114, 4085};
    private static final int[] sellItemList11_ids = {8331, 8341, 8342, 8346, 8349, 5539, 5536, 5535, 5543, 5547, 5542, 5532, 5496, 5520, 5524, 5508, 5525, 5478, 5486, 5494, 5482, 6341, 6343, 6345, 5526, 5497, 5509, 5489, 5479, 5521, 5485, 5548, 5545, 5538, 5529, 5533, 5522, 5502, 5514, 5527, 5480, 5503, 5528, 5515, 6342, 5481, 6344, 5523, 6346, 5488, 5487, 5495, 5534, 5537, 5546, 5541, 5544, 8712, 8713, 8714, 8715, 8716, 8717, 8718, 8719, 8720, 8721, 8722};
    private static final int[] sellItemList12_ids = {6688, 6689, 6690, 6691, 6693, 6694, 6695, 6696, 6697, 7579, 6698, 6699, 6700, 6701, 6702, 6703, 6704, 6705, 6706, 6707, 6708, 6709, 6710, 6711, 6712, 6713, 6714, 6908, 6909, 6910, 6911, 6912, 6913, 6914, 6915, 6916, 6904, 6905, 6906, 6907, 7684, 7685, 7686, 7687, 7688, 7697, 7698, 9530, 9531, 9532, 9533, 9534, 9535, 9536, 9537, 9538, 9539, 9540, 9541, 9542, 9543, 9544, 9545, 9991, 9992, 9993, 9616, 9617, 9618, 9619, 9620, 9621, 9622, 9623, 9624, 10546, 10547, 9628, 9629, 9630, 10397, 10398, 10399, 10400, 10401, 10402, 10403, 10404, 10405, 10114, 13099, 15634, 15635, 15636, 15637, 15638, 15639, 15640, 15641, 15642, 15643, 15644, 15645, 15646, 15647, 15648, 15649, 15650, 15651, 15652, 15653, 15654, 15655, 15656, 15657, 15658, 15659, 15660, 15661, 15662, 15663, 15664, 15665, 15666, 15667, 15668, 15669, 15670, 15671, 15672, 15673, 15674, 15675, 15691, 15692, 15693, 15769, 15770, 15771, 15772, 15773, 15774, 16122, 16123, 16124, 16125, 16126, 16127, 16128, 16129, 16130, 16131, 16132, 16133};
    private static final int[] sellItemList13_ids = {1048, 1049, 1050, 1051, 1052, 1053, 1054, 1055, 1056, 1058, 1095, 1096, 1097, 1098, 1099, 1152, 1294, 1370, 1371, 1372, 1377, 1378, 1379, 1380, 1381, 1382, 1383, 1384, 1385, 1386, 1387, 1388, 1389, 1390, 1391, 1392, 1394, 1397, 1398, 1399, 1400, 1401, 1402, 1403, 1404, 1405, 1406, 1407, 1408, 1409, 1410, 1411, 1412, 1413, 1414, 1415, 1416, 1417, 1418, 1512, 1513, 1514, 1515, 1516, 1517, 1518, 1519, 1520, 1521, 1522, 1523, 1524, 1525, 1526, 1527, 1528, 1529, 1530, 1531, 1532, 1533, 1534, 1535, 1536, 1537, 1667, 1668, 1669, 1670, 1671, 1856, 3038, 3039, 3040, 3041, 3042, 3043, 3044, 3045, 10025, 10026, 10027, 10028, 10029, 10031, 10032, 10033, 10058, 10072, 10073, 10074, 10075, 10076, 10077, 10078, 10079, 10094, 10180, 10181};
    private static final int[] sellItemList14_ids = {3046, 3047, 3048, 3049, 3050, 3051, 3052, 3053, 3054, 3055, 3056, 3057, 3058, 3059, 3060, 3061, 3062, 3063, 3064, 3065, 3066, 3067, 3068, 3069, 3070, 3071, 3072, 3073, 3074, 3075, 3076, 3077, 3078, 3079, 3080, 3081, 3082, 3083, 3084, 3085, 3086, 3087, 3088, 3089, 3090, 3091, 3092, 3093, 3094, 3095, 3096, 3097, 3098, 3099, 3100, 3101, 3102, 3103, 3104, 3105, 3106, 3107, 3108, 3109, 3110, 3111, 3112, 3113, 3114, 3115, 3116, 3117, 3118, 3429, 3430, 3431, 3432, 3940, 3941, 3942, 3943, 3944, 4200, 4201, 4203, 4204, 4205, 4206, 4207, 4208, 4906, 4907, 4908, 4909, 4910, 4911, 4912, 4913, 4914, 4915, 10036, 10037, 10038, 10039, 10043, 10044, 10047, 10048, 10049, 10050, 10052, 10053, 10054, 10055, 10057, 10059, 10060, 10061, 10062, 10063, 10064, 10065, 10071, 10080, 10082, 10083, 10084, 10087, 10089, 10092, 10093, 10095, 10098, 10030, 10034, 10040, 10041, 10042, 10045, 10046, 10052, 10066, 10081, 10085, 10086, 10088, 10097, 10181, 10182, 10184, 10185, 10186, 10187, 10188, 10189, 10190, 10191, 10192, 10193, 10194, 10195, 10196, 10204, 10183};
    private static final int[] sellItemList15_ids = {4916, 4917, 4918, 4919, 4920, 4921, 4922, 4923, 4924, 4925, 4926, 4927, 4928, 4929, 4930, 4931, 4932, 4933, 4934, 5013, 5014, 5015, 5809, 5810, 5811, 5812, 5813, 5814, 5815, 5816, 6350, 6351, 6352, 6395, 6396, 6397, 6398, 7638, 7639, 7640, 7641, 7642, 7643, 7644, 7645, 7646, 7647, 7648, 7649, 7650, 7651, 7652, 7653, 7654, 7655, 7656, 7657, 7658, 7659, 7660, 7661, 7662, 7663, 7664, 7665, 7666, 7667, 7668, 7669, 7670, 7671, 7672, 7673, 7674, 7675, 7676, 8380, 8381, 8382, 8383, 8384, 8385, 8386, 8387, 8388, 8389, 8390, 8391, 8392, 8393, 8394, 8395, 8396, 8397, 8398, 8399, 8400, 8401, 8402, 8877, 8878, 8879, 8880, 8881, 8882, 8883, 8884, 8885, 8886, 8887, 8888, 8889, 8890, 8891, 8892, 8893, 8894, 8895, 8896, 8897, 8898, 8899, 8900, 8901, 8902, 8903, 8904, 8905, 8906, 8907, 8908, 8909, 8945, 8946, 8616, 10099, 10100, 10101, 10102, 10103, 10104, 10105, 10106, 10107, 10108, 10109, 10068, 10069, 10090, 10091, 10203, 10557, 10558, 10579, 10580, 10581, 10582, 10583, 10584, 10590, 10596, 10597, 10598, 10599, 10600, 10601, 10602, 10603, 10604, 10605, 10302, 10303, 10304, 10305, 10306, 10608, 10609, 10610, 7835};
    private int type = 0;

    public test_server_helper(NpcInstance actor) {
        super(actor);
        type = actor.getParameter("helperType", 0);
        if (!locked) {
            locked = true;
            initSellList();
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (ask == 100500) {
            if (CUSTOM_BUFF) {
                if (reply == 100500) // warrior
                {
                    for (int i = 0; i < 19; i++) {
                        getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(7041 + i, 1));
                    }
                    getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(7064, 1));
                } else if (reply == 100501) // magic
                {
                    for (int i = 0; i < 19; i++) {
                        getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(7041 + i, 1));
                    }
                    getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(7061, 1));
                }
            }
        }

        if (ask == -4 && reply == 1) {
            if (type == 0)
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper001a.htm");
            else
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper001b.htm");
        } else if (ask == -6 && reply == 1) {
            if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael && player.getQuestState(236) != null && !player.getQuestState(236).isCompleted()) {
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper031");
                return;
            }
            if (player.isNoble()) {
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper025b.htm");
                return;
            }
            if (!player.getPlayerClassComponent().isSubClassActive()) {
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper025a.htm");
                return;
            }
            if (player.getPlayerClassComponent().getActiveClass().getLevel() < 75) {
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper025a.htm");
                return;
            }
            Olympiad.addNoble(player);
            player.setNoble(true);
            player.updatePledgeClass();
            player.updateNobleSkills();
            player.sendPacket(new SkillList(player));
            player.broadcastUserInfo(true);
            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper025.htm");
        } else if (ask == -7 && reply == 1) {
            if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.First)) {
                if (player.getLevel() < 20) {
                    actor.showChatWindow(player, "pts/testserverhelper/test_server_helper027.htm");
                    return;
                }
                if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.human) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case fighter: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026a.htm");
                            break;
                        }
                        case mage: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026b.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case elven_fighter: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026c.htm");
                            break;
                        }
                        case elven_mage: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026d.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.darkelf) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case dark_fighter: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026e.htm");
                            break;
                        }
                        case dark_mage: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026f.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.orc) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case orc_fighter: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026g.htm");
                            break;
                        }
                        case orc_mage: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026h.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case dwarven_fighter: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper026i.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case kamael_m_soldier: {
                            player.getPlayerClassComponent().setClassId(ClassId.trooper.getId(), false, true);
                            actor.showChatWindow(player, fnAfterClassChange);
                            break;
                        }
                        case kamael_f_soldier: {
                            player.getPlayerClassComponent().setClassId(ClassId.warder.getId(), false, true);
                            actor.showChatWindow(player, fnAfterClassChange);
                            break;
                        }
                    }
                }
            } else if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Second))
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper028.htm");
            else if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Third))
                actor.showChatWindow(player, fnYouAreThirdClass);
            else if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Fourth))
                actor.showChatWindow(player, fnYouAreFourthClass);
        } else if (ask == -2 && reply == 1) {
            if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Second)) {
                if (player.getLevel() < 40) {
                    actor.showChatWindow(player, fnLowLevel);
                    return;
                }
                if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.human) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case fighter: {
                            actor.showChatWindow(player, fnHumanFighter);
                            break;
                        }
                        case warrior: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper012a.htm");
                            break;
                        }
                        case knight: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper012b.htm");
                            break;
                        }
                        case rogue: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper012c.htm");
                            break;
                        }
                        case mage: {
                            actor.showChatWindow(player, fnHumanMage);
                            break;
                        }
                        case wizard: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper013a.htm");
                            break;
                        }
                        case cleric: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper013b.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case elven_fighter: {
                            actor.showChatWindow(player, fnElfFighter);
                            break;
                        }
                        case elven_knight: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper014a.htm");
                            break;
                        }
                        case elven_scout: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper014b.htm");
                            break;
                        }
                        case elven_mage: {
                            actor.showChatWindow(player, fnElfMage);
                            break;
                        }
                        case elven_wizard: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper015a.htm");
                            break;
                        }
                        case oracle: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper015b.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.darkelf) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case dark_fighter: {
                            actor.showChatWindow(player, fnDElfFighter);
                            break;
                        }
                        case palus_knight: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper016a.htm");
                            break;
                        }
                        case assassin: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper016b.htm");
                            break;
                        }
                        case dark_mage: {
                            actor.showChatWindow(player, fnDElfMage);
                            break;
                        }
                        case dark_wizard: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper017a.htm");
                            break;
                        }
                        case shillien_oracle: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper017b.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.orc) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case orc_fighter: {
                            actor.showChatWindow(player, fnOrcFighter);
                            break;
                        }
                        case orc_raider: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper018a.htm");
                            break;
                        }
                        case orc_monk: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper018b.htm");
                            break;
                        }
                        case orc_mage: {
                            actor.showChatWindow(player, fnOrcMage);
                            break;
                        }
                        case orc_shaman: {
                            actor.showChatWindow(player, fnOrcMage);
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case dwarven_fighter: {
                            actor.showChatWindow(player, fnDwarfFighter);
                            break;
                        }
                        case artisan: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper020b.htm");
                            break;
                        }
                        case scavenger: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper020a.htm");
                            break;
                        }
                    }
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael) {
                    switch (player.getPlayerClassComponent().getClassId()) {
                        case trooper: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper020c.htm");
                            break;
                        }
                        case warder: {
                            actor.showChatWindow(player, "pts/testserverhelper/test_server_helper020d.htm");
                            break;
                        }
                    }
                }
            } else if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Third))
                actor.showChatWindow(player, fnYouAreThirdClass);
            else if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Fourth))
                actor.showChatWindow(player, fnYouAreFourthClass);
            else
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper029.htm");
        }
        if (ask == -5 && reply == 1) {
            if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Third) && player.getLevel() > 75) {
                switch (player.getPlayerClassComponent().getClassId()) {
                    case gladiator: {
                        player.getPlayerClassComponent().setClassId(ClassId.duelist.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case warlord: {
                        player.getPlayerClassComponent().setClassId(ClassId.dreadnought.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case paladin: {
                        player.getPlayerClassComponent().setClassId(ClassId.phoenix_knight.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case dark_avenger: {
                        player.getPlayerClassComponent().setClassId(ClassId.hell_knight.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case treasure_hunter: {
                        player.getPlayerClassComponent().setClassId(ClassId.adventurer.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case hawkeye: {
                        player.getPlayerClassComponent().setClassId(ClassId.sagittarius.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case sorceror: {
                        player.getPlayerClassComponent().setClassId(ClassId.archmage.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case necromancer: {
                        player.getPlayerClassComponent().setClassId(ClassId.soultaker.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case warlock: {
                        player.getPlayerClassComponent().setClassId(ClassId.arcana_lord.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case bishop: {
                        player.getPlayerClassComponent().setClassId(ClassId.cardinal.getId(), false, true);
                        player.getQuestState(255).giveItems(potion_of_experience_bishop, 1);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case prophet: {
                        player.getPlayerClassComponent().setClassId(ClassId.hierophant.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case temple_knight: {
                        player.getPlayerClassComponent().setClassId(ClassId.eva_templar.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case sword_singer: {
                        player.getPlayerClassComponent().setClassId(ClassId.sword_muse.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case plains_walker: {
                        player.getPlayerClassComponent().setClassId(ClassId.wind_rider.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case silver_ranger: {
                        player.getPlayerClassComponent().setClassId(ClassId.moonlight_sentinel.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case spellsinger: {
                        player.getPlayerClassComponent().setClassId(ClassId.mystic_muse.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case elemental_summoner: {
                        player.getPlayerClassComponent().setClassId(ClassId.elemental_master.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case elder: {
                        player.getPlayerClassComponent().setClassId(ClassId.eva_saint.getId(), false, true);
                        player.getQuestState(255).giveItems(potion_of_experience_elder, 1);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case shillien_knight: {
                        player.getPlayerClassComponent().setClassId(ClassId.shillien_templar.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case bladedancer: {
                        player.getPlayerClassComponent().setClassId(ClassId.spectral_dancer.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case abyss_walker: {
                        player.getPlayerClassComponent().setClassId(ClassId.ghost_hunter.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case phantom_ranger: {
                        player.getPlayerClassComponent().setClassId(ClassId.ghost_sentinel.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case spellhowler: {
                        player.getPlayerClassComponent().setClassId(ClassId.storm_screamer.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case phantom_summoner: {
                        player.getPlayerClassComponent().setClassId(ClassId.spectral_master.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case shillien_elder: {
                        player.getPlayerClassComponent().setClassId(ClassId.shillien_saint.getId(), false, true);
                        player.getQuestState(255).giveItems(potion_of_experience_silien_elder, 4);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case destroyer: {
                        player.getPlayerClassComponent().setClassId(ClassId.titan.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case tyrant: {
                        player.getPlayerClassComponent().setClassId(ClassId.grand_khauatari.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case overlord: {
                        player.getPlayerClassComponent().setClassId(ClassId.dominator.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case warcryer: {
                        player.getPlayerClassComponent().setClassId(ClassId.doomcryer.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case bounty_hunter: {
                        player.getPlayerClassComponent().setClassId(ClassId.fortune_seeker.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case warsmith: {
                        player.getPlayerClassComponent().setClassId(ClassId.maestro.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case berserker: {
                        player.getPlayerClassComponent().setClassId(ClassId.doombringer.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case m_soul_breaker: {
                        player.getPlayerClassComponent().setClassId(ClassId.m_soul_hound.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case f_soul_breaker: {
                        player.getPlayerClassComponent().setClassId(ClassId.f_soul_hound.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case arbalester: {
                        player.getPlayerClassComponent().setClassId(ClassId.trickster.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                    case inspector: {
                        player.getPlayerClassComponent().setClassId(ClassId.judicator.getId(), false, true);
                        player.getQuestState(255).soundEffect("ItemSound.quest_fanfare_2");
                        actor.showChatWindow(player, fnAfterClassChange);
                        break;
                    }
                }
            } else if (player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Fourth))
                actor.showChatWindow(player, fnYouAreFourthClass);
            else
                actor.showChatWindow(player, "pts/testserverhelper/test_server_helper024.htm");
        }
        if (ask == -3) {
            switch (reply) {
                case 0:
                    if (player.getLevel() < 10)
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl002.htm");
                    else if (player.getClan() != null && player.isClanLeader())
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl003.htm");
                    else if (player.getClan() != null && !player.isClanLeader())
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl004.htm");
                    else
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl005.htm");
                    break;
                case 1:
                    if (player.getClan() != null && player.isClanLeader())
                        actor.showChatWindow(player, fnPledgeLevelUP);
                    else
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl014.htm");
                    break;
                case 2:
                    if (player.getClan() != null && player.isClanLeader())
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl007.htm");
                    else
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl008.htm");
                    break;
                case 3:
                    if (player.getClan() != null && player.isClanLeader())
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl010.htm");
                    else
                        actor.showChatWindow(player, "pts/testserverhelper/clan/pl011.htm");
                    break;
                case 4:
                    levelUpClan(player);
                    break;
            }
        }
        if (ask == -1) {
            switch (reply) {
                case 0:
                    if (type == 0)
                        ShowBuySell(player, 10000000);
                    else
                        ShowBuySell(player, 10000008);
                    break;
                case 1:
                    if (type == 0)
                        ShowBuySell(player, 10000001);
                    else
                        ShowBuySell(player, 10000009);
                    break;
                case 2:
                    if (type == 0)
                        ShowBuySell(player, 10000002);
                    else
                        ShowBuySell(player, 10000010);
                    break;
                case 3:
                    if (type == 0)
                        ShowBuySell(player, 10000003);
                    else
                        ShowBuySell(player, 10000011);
                    break;
                case 4:
                    if (type == 0)
                        ShowBuySell(player, 10000004);
                    else
                        ShowBuySell(player, 10000012);
                    break;
                case 5:
                    if (type == 0)
                        ShowBuySell(player, 10000005);
                    else
                        ShowBuySell(player, 10000013);
                    break;
                case 6:
                    if (type == 0)
                        ShowBuySell(player, 10000006);
                    else
                        ShowBuySell(player, 10000014);
                    break;
                case 7:
                    if (type == 0)
                        ShowBuySell(player, 10000007);
                    else
                        ShowBuySell(player, 10000015);
                    break;
            }
        }
        if (ask == -8 && reply == 1) {
            if (player != null)
                giveAllSkills(player);
        }
        if (ask == -1006) {
            if (reply == 1) {
                switch (type) {
                    case 0:
                        actor.showChatWindow(player, "pts/testserverhelper/test_server_helper001a.htm");
                        break;
                    case 1:
                        actor.showChatWindow(player, "pts/testserverhelper/test_server_helper001b.htm");
                        break;
                }
            }
        }
        if (ask == -303) {
            if (reply == 622)
                MultiSellHolder.getInstance().SeparateAndSend(622, player, actor.getObjectId(), 0);
            else if (reply == 644)
                MultiSellHolder.getInstance().SeparateAndSend(644, player, actor.getObjectId(), 0);
            else if (reply == 695)
                MultiSellHolder.getInstance().SeparateAndSend(695, player, actor.getObjectId(), 0);
            else if (reply == 696)
                MultiSellHolder.getInstance().SeparateAndSend(696, player, actor.getObjectId(), 0);
            else if (reply == 697)
                MultiSellHolder.getInstance().SeparateAndSend(697, player, actor.getObjectId(), 0);
        }
        if (ask == -999) {
            if (!(ClassId.VALUES[reply].equalsOrChildOf(ClassId.VALUES[player.getPlayerClassComponent().getActiveClassId()]))) {
                Log.audit(player.getAccountName(), "Used replaced bypass. Change class in Test Server Helper");
                player.sendActionFailed();
                return;
            } else {
                player.getPlayerClassComponent().setClassId(reply, false, true);
                actor.showChatWindow(player, fnAfterClassChange);
            }
        }
    }

    private void giveAllSkills(final Player player) {
        int unLearnable = 0;
        Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
        while (skills.size() > unLearnable) {
            unLearnable = 0;
            for (final SkillLearn s : skills) {
                final SkillEntry sk = SkillTable.getInstance().getSkillEntry(s.getId(), s.getLevel());
                if (sk == null || !sk.getTemplate().getCanLearn(player.getPlayerClassComponent().getClassId())) {
                    unLearnable++;
                    continue;
                }
                player.addSkill(sk, true);
            }
            skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
        }
        player.sendPacket(new SkillList(player));
        final String message = player.getLanguage() == Language.ENGLISH ? "All skills for you level has ben added." : "Вам были добавлены все умения вашего уровня";
        player.sendAdminMessage(message);
    }

    private void ShowBuySell(final Player player, final int sellListId) {
        final BuyList sellList = getActor().getTemplate().getTradeList(sellListId);
        if (sellList != null && sellList.getNpcId() == getActor().getNpcId())
            player.sendPacket(new ExBuyList(sellList, player), new ExBuySellList(player, false));
    }

    public void initSellList() {
        addItemToList(sellItemList0_ids, 10000000, 31756);
        addItemToList(sellItemList1_ids, 10000001, 31756);
        addItemToList(sellItemList2_ids, 10000002, 31756);
        addItemToList(sellItemList3_ids, 10000003, 31756);
        addItemToList(sellItemList4_ids, 10000004, 31756);
        addItemToList(sellItemList5_ids, 10000005, 31756);
        addItemToList(sellItemList6_ids, 10000006, 31756);
        addItemToList(sellItemList7_ids, 10000007, 31756);
        addItemToList(sellItemList8_ids, 10000008, 31757);
        addItemToList(sellItemList9_ids, 10000009, 31757);
        addItemToList(sellItemList10_ids, 10000010, 31757);
        addItemToList(sellItemList11_ids, 10000011, 31757);
        addItemToList(sellItemList12_ids, 10000012, 31757);
        addItemToList(sellItemList13_ids, 10000013, 31757);
        addItemToList(sellItemList14_ids, 10000014, 31757);
        addItemToList(sellItemList15_ids, 10000015, 31757);
    }

    private void addItemToList(final int[] list, final int sellListId, final int npcId) {
        final BuyList sellList = new BuyList(sellListId, npcId);
        for (final int itemId : list) {
            final ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(itemId);
            if (template == null) {
                LOGGER.warn("addItemToList(int[]) returned is empty list:={}", list);
                return;
            }
            final long price = template.getReferencePrice();
            final Product product = new Product();
            product.setItem(template);
            product.setCount(0);
            product.setPrice(price * 3);
            sellList.addProduct(product);
        }
        NpcHolder.getInstance().getTemplate(npcId).addTradeList(sellListId, sellList);
    }

    public void levelUpClan(final Player player) {
        final Clan clan = player.getClan();
        if (clan == null) {
            return;
        }
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }

        boolean increaseClanLevel = false;

        switch (clan.getLevel()) {
            case 0:
                // Upgrade to 1
                increaseClanLevel = true;
                break;
            case 1:
                // Upgrade to 2
                increaseClanLevel = true;
                break;
            case 2:
                // Upgrade to 3
                increaseClanLevel = true;
                break;
            case 3:
                // Upgrade to 4
                increaseClanLevel = true;
                break;
            case 4:
                // Upgrade to 5
                increaseClanLevel = true;
                break;
        }

        if (increaseClanLevel) {
            clan.setLevel(clan.getLevel() + 1);
            clan.updateClanInDB();

            player.broadcastCharInfo();

            getActor().doCast(SkillTable.getInstance().getSkillEntry(5103, 1), player, true);

            if (clan.getLevel() >= 4) {
                SiegeUtils.addSiegeSkills(player);
            }

            if (clan.getLevel() == 5) {
                player.sendPacket(SystemMsg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
            }

            // notify all the members about it
            final PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
            final PledgeStatusChanged ps = new PledgeStatusChanged(clan);
            for (final UnitMember mbr : clan) {
                if (mbr.isOnline()) {
                    mbr.getPlayer().updatePledgeClass();
                    mbr.getPlayer().sendPacket(SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED, pu, ps);
                    mbr.getPlayer().broadcastCharInfo();
                }
            }
        } else {
            player.sendPacket(SystemMsg.CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL);
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}