// this class is used to save each word after stemming and it's number of occurence in each tag in an object called (Word)
// then we save this word later in the database
public class Word {
	int h1=0;
	int h2=0;
	int h3=0;
	int h4=0;
	int h5=0;
	int h6=0;
	int p=0;
	int title=0;
	int bold=0;
	int italic=0;
	String word=null;
	public Word(String w) {
		this.word=w;
		 this.h1=0;this.h2=0;this.h3=0;this.h4=0;this.h5=0;this.h6=0;this.p=0;this.bold=0;this.italic=0;
	}
	public void incrementOccurence(String pos) {
		if(pos=="h1")h1=h1+1;
		else if(pos=="h2")h2=h2+1;
		else if(pos=="h3")h3=h3+1;
		else if(pos=="h4")h4=h4+1;
		else if(pos=="h5")h5=h5+1;
		else if(pos=="h6")h6=h6+1;
		else if(pos=="p")p=p+1;
		else if(pos=="bold")bold=bold+1;
		else if(pos=="italic")italic=italic+1;
		else if(pos=="title") title=title+1;
	}
	public void print() {
		System.out.print("["+this.word+" "+this.h1+" "+this.h2+" "+this.h3+" "+this.h4+" "+this.h5+" "+this.h6+" "+
				this.p+" "+this.bold+" "+this.italic+" "+this.title+"]\n");
	}

}
