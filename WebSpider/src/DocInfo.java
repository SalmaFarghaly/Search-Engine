
public class DocInfo{
		String link;
		double tfIdf;
		DocInfo(String l,double t){
			this.link = l;
			this.tfIdf = t;
		}
		public void addTFIDF(double t) {
			this.tfIdf +=t;
		}
		public void print() {
			 System.out.print("link: "+this.link+" TF-IDF: "+this.tfIdf);
			 System.out.print("\r\n");
			 
		}
	}