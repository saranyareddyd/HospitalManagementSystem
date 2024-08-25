package HSPMS;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HosMS {
    private static final String URL="jdbc:mysql://localhost:3306/hospital";
    private static final String User="root";
    private static final String password="saranya@2003";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner=new Scanner(System.in);
        try{
            Connection connection=DriverManager.getConnection(URL, User, password);
            Patient patient=new Patient(connection, scanner);
            Doctor doctor=new Doctor(connection);
            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println(" 1.Add Patients");
                System.out.println(" 2.View Patients");
                System.out.println(" 3.View Doctors");
                System.out.println(" 4.Book Appointment");
                System.out.println("5.Exit");
                System.out.println("Enter your choice:");
                int choice=scanner.nextInt();
                switch (choice) {
                    case 1:
                        //add patient
                        patient.addPatient();
                        break;
                    case 2:
                        //view patiet
                        patient.viewPatient();
                        break;
                    case 3:
                        //view doctors
                        doctor.viewDoctor();
                        break; 
                    case 4:
                        //book appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        break;
                    case 5:
                        //exit
                        return;
                       

                    default:
                        break;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }


    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.print("Enter Patient id: ");
        int pat_id=scanner.nextInt();
        System.out.print("Enter Doctor id: ");
        int doct_id=scanner.nextInt();
        System.out.print("Enter appointment sate(YYYY-MM-DD)");
        String appointmentDate=scanner.next();
        if(patient.getPatientById(pat_id) && doctor.getDoctorById(doct_id)){
            if(checkDoctorAvailability(doct_id,appointmentDate,connection)){
                String appointmentQuer="Insert into appoinments(pat_id,doc_id,app_date) values(?,?,?)";
                try{
                    PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuer);
                    preparedStatement.setInt(1, pat_id);
                    preparedStatement.setInt(2, doct_id);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected=preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked!");
                    }
                    else{
                        System.out.println("Failed to book Appointment!!");
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Doctor not available on this date !!");
            }
        }
        else{
            System.out.println("Either doctor or patient doesn't exist!!!");
        }

    }

    public static boolean checkDoctorAvailability(int doc_id,String appointmentdate,Connection connection){
        String query ="SELECT COUNT(*) FROM appointment Where doc_id=? AND app_date=?";
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1, doc_id);
            preparedStatement.setString(2, appointmentdate);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                int count=resultSet.getInt(1);
                if(count==0){
                    return true;
                }
                else{
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
