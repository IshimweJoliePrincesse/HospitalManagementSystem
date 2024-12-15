class Person{
    
    Person(){
        System.out.println("Person class Constructor");
    }
}

class Student extends Person{
    Student(){
        super();

        System.out.println("Student class constructor");
    }
}

class Main{
    
        public static void main(String[] args){
            Student s = new Student();
            Person p = new Person();
        }
    
}