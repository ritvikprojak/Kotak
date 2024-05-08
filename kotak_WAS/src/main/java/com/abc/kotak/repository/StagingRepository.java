package com.abc.kotak.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abc.kotak.model.Staging;

public interface StagingRepository extends JpaRepository<Staging, Long> {

	@Query(value = "select * from  HRUPM.HRMS_HRRM stag where stag.DOMAIN_LOGIN_ID =?1 AND (stag.last_working_date >= localtimestamp\r\n"
			+ "          OR stag.last_working_date IS NULL) ", nativeQuery = true)
	Staging findUserDomain(String id);

	@Query(value = "select * from  HRUPM.HRMS_HRRM stag where stag.DOMAIN_LOGIN_ID =?1", nativeQuery = true)
	Staging findUserDomainForMapping(String id);

	@Query(value = "select  count(*) from  HRUPM.HRMS_HRRM stag where stag.EMPLOYEE_NUMBER =?1", nativeQuery = true)
	int findUserID(String id);

	@Query(value = "select  HR_PERSON_NUMBER from  HRUPM.HRMS_HRRM stag where stag.EMPLOYEE_NUMBER =?1", nativeQuery = true)
	String findEmployeeHR(String id);

	/*
	 * @Query(
	 * value="select  EMPLOYEE_NUMBER from  HRUPM.HRMS_HRRM stag where stag.LOB_CODE =?1 AND stag.LOC_CODE =?2 AND stag.CC_CODE =?3 AND stag.DOJ = TO_DATE(?4 , 'yyyy/MM/dd')"
	 * ,nativeQuery = true) List<String> findEmployeeByDOJ(String LOB,String LOC,
	 * String CC, String DOJ);
	 * 
	 * @Query(
	 * value="select  * from  HRUPM.HRMS_HRRM stag where stag.LOB_CODE =?1 AND stag.LOC_CODE =?2 AND stag.CC_CODE =?3 AND stag.DOJ_KOTAK_GROUP = TO_DATE(?4 , 'yyyy/MM/dd')"
	 * ,nativeQuery = true) List<Staging> findEmployeeByDOGJ(String LOB,String LOC,
	 * String CC, String DOGJ);
	 * 
	 * @Query(
	 * value="select  EMPLOYEE_NUMBER from  HRUPM.HRMS_HRRM stag where stag.LOB_CODE =?1 AND stag.LOC_CODE =?2 AND stag.CC_CODE =?3 AND stag.LAST_WORKING_DATE = TO_DATE(?4 , 'yyyy/MM/dd')"
	 * ,nativeQuery = true) List<String> findEmployeeByLastDate(String LOB,String
	 * LOC, String CC, String LastWorkDate);
	 * 
	 * @Query(
	 * value="select  EMPLOYEE_NUMBER from  HRUPM.HRMS_HRRM stag where stag.LOB_CODE =?1 AND stag.LOC_CODE =?2 AND stag.CC_CODE =?3"
	 * ,nativeQuery = true) List<String> findEmployeeStatusAll(String LOB,String
	 * LOC, String CC);
	 * 
	 * @Query(
	 * value="select  EMPLOYEE_NUMBER from  HRUPM.HRMS_HRRM stag where stag.LOB_CODE =?1 AND stag.LOC_CODE =?2 AND stag.CC_CODE =?3 AND stag.LAST_WORKING_DATE >= TO_DATE(?4 , 'yyyy/MM/dd')"
	 * ,nativeQuery = true) List<String> findEmployeeIsActive(String LOB,String LOC,
	 * String CC , String today);
	 * 
	 * @Query(
	 * value="select  EMPLOYEE_NUMBER from  HRUPM.HRMS_HRRM stag where stag.LOB_CODE =?1 AND stag.LOC_CODE =?2 AND stag.CC_CODE =?3 AND stag.LAST_WORKING_DATE <= TO_DATE(?4 , 'yyyy/MM/dd')"
	 * ,nativeQuery = true) List<String> findEmployeeIsInActive(String LOB,String
	 * LOC, String CC, String today);
	 */
	@Query(value = "select * from  HRUPM.HRMS_HRRM stag where stag.EMPLOYEE_NUMBER =?1", nativeQuery = true)
	Staging findUserByEmpNumber(String loggedInUserID);

	@Query(value = "select distinct PERSON_NUMBER from HRUPM.HRRM", nativeQuery = true)
	List<String> findHrAvailable();

	@Query(value = "UPDATE HRUPM.HRMS_HRRM SET ROLE=?1 WHERE DOMAIN_LOGIN_ID=?2 AND EMPLOYEE_NUMBER=?3", nativeQuery = true)
	void updateHRRMS(String role, String userId, String employeeNumber);

}
