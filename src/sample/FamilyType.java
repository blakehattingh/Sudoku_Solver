package sample;

public enum FamilyType {
	
	ROW{
		@Override
		public String toString() {
			return "Row";
		}
	}, COLUMN{
		@Override
		public String toString() {
			return "Column";
		}
	}, BOX{
		@Override
		public String toString() {
			return "Box";
		}
	}

}
