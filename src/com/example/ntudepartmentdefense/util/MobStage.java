package com.example.ntudepartmentdefense.util;

public class MobStage {
	
	public static final int     EE_INDEX = 0;
	public static final int    MED_INDEX = 1;
	
	public static final short  TAG_BULKY  = 0;
	public static final short  TAG_FAST   = 1;
	public static final short  TAG_AMBUSH = 2;
	public static final short  TAG_LETHAL = 3;
	public static final short  TAG_WEALTHY= 4;
	public static final short  TAG_REFRESH= 5;
	public static final short  TAG_BOSS   = 6;
	public static final short  TAG_TRINITY= 7;
	public static final short  TAG_CROWDED= 8;
	
	public static final int[][] MOB_SPAWN_ATTR = {
		// SpawnGap,     SpawnCD,    WaveSpawn
		{		300,		  30,			15, }, //EE
		{		300,		  30,			15, }, //MED

	};
	public static final short[][][] BASIC_TAGS = {
		// EE_BASIC
		{
			{},						// Wave 1
			{ TAG_BULKY },			// Wave 2
			{ TAG_FAST },			// Wave 3	
			{ TAG_CROWDED },		// Wave 4
			{ TAG_AMBUSH },			// Wave 5
			{ TAG_LETHAL },			// Wave 6
			{ TAG_REFRESH },		// Wave 7
			{ TAG_WEALTHY },		// Wave 8
			{ TAG_TRINITY },		// Wave 9
			{ TAG_BOSS },			// Wave 10
		},

		// MED_BASIC
		{
			{},						// Wave 1
			{ TAG_BULKY },			// Wave 2
			{ TAG_FAST },			// Wave 3	
			{ TAG_CROWDED },		// Wave 4
			{ TAG_AMBUSH },			// Wave 5
			{ TAG_LETHAL },			// Wave 6
			{ TAG_REFRESH },		// Wave 7
			{ TAG_WEALTHY },		// Wave 8
			{ TAG_TRINITY },		// Wave 9
			{ TAG_BOSS },			// Wave 10
		}
	};

	public static final short[][][] ADVANCED_TAGS = {
		// EE_ADVANCED
		{
			{ TAG_BOSS,		 TAG_REFRESH   },	// Wave 10
			{},									// Wave 1
			{ TAG_BULKY,     TAG_CROWDED,  },	// Wave 2
			{ TAG_FAST,      TAG_CROWDED,  },	// Wave 3	
			{ TAG_CROWDED,   TAG_AMBUSH,   },	// Wave 4
			{ TAG_AMBUSH,    TAG_FAST,     },	// Wave 5
			{ TAG_LETHAL, 	 TAG_AMBUSH,   },	// Wave 6
			{ TAG_REFRESH,   TAG_CROWDED,  },	// Wave 7
			{ TAG_WEALTHY,	 TAG_BULKY,	   },	// Wave 8
			{ TAG_TRINITY,	 TAG_FAST,	   },	// Wave 9
		},

		// MED_ADVANCED
		{
			{ TAG_BOSS,		 TAG_REFRESH   },	// Wave 10
			{},									// Wave 1
			{ TAG_BULKY,     TAG_CROWDED,  },	// Wave 2
			{ TAG_FAST,      TAG_CROWDED,  },	// Wave 3	
			{ TAG_CROWDED,   TAG_AMBUSH,   },	// Wave 4
			{ TAG_AMBUSH,    TAG_FAST,     },	// Wave 5
			{ TAG_LETHAL, 	 TAG_AMBUSH,   },	// Wave 6
			{ TAG_REFRESH,   TAG_CROWDED,  },	// Wave 7
			{ TAG_WEALTHY,	 TAG_BULKY,	   },	// Wave 8
			{ TAG_TRINITY,	 TAG_FAST,	   },	// Wave 9
		}
	};
	
	public static short[] getTags( int departmentID , int wave) {
		if ( wave > BASIC_TAGS[departmentID].length) {
			return ADVANCED_TAGS[departmentID][ ( wave - BASIC_TAGS[departmentID].length) 
			                     % ADVANCED_TAGS[departmentID].length  ];
		}
		return BASIC_TAGS[departmentID][wave-1];
	}
	
	
}



