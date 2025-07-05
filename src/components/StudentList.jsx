import React, { useEffect, useState } from 'react';

function StudentList() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newStudent, setNewStudent] = useState({ name: '', age: '', course: '' });

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const studentsPerPage = 2;

  // API URLs
  const apiUrl = import.meta.env.VITE_API_BASE_URL;
  const apiDelete = import.meta.env.VITE_API_BASE_Delete;
  const apiUpdate = import.meta.env.VITE_API_BASE_update;
  const apiStudentById = import.meta.env.VITE_API_BASE_StudentById;
  const apiAdd = import.meta.env.VITE_API_BASE_ADD;

  // Fetch all students
  const fetchStudents = () => {
    fetch(apiUrl)
      .then(res => res.json())
      .then(data => {
        setStudents(data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Error fetching students:", err);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  // Delete student
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure to delete?")) return;
    try {
      const res = await fetch(`${apiDelete}/students/${id}`, { method: 'DELETE' });
      if (res.ok) {
        setStudents(prev => prev.filter(s => s.id !== id));
        alert("Deleted successfully.");
      } else {
        alert("Delete failed.");
      }
    } catch (err) {
      console.error("Delete error:", err);
    }
  };

  // Load student for update
  const handleUpdate = async (id) => {
    try {
      const res = await fetch(`${apiStudentById}/students/${id}`);
      const data = await res.json();
      setSelectedStudent(data);
      setShowUpdateModal(true);
    } catch (err) {
      console.error("Fetch error:", err);
      alert("Cannot load student.");
    }
  };

  const handleUpdateChange = (e) => {
    const { name, value } = e.target;
    setSelectedStudent(prev => ({
      ...prev,
      [name]: name === 'age' ? parseInt(value) : value,
    }));
  };

  const handleUpdateSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${apiUpdate}/students/${selectedStudent.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(selectedStudent),
      });
      if (res.ok) {
        const updated = await res.json();
        setStudents(prev => prev.map(s => (s.id === updated.id ? updated : s)));
        alert("Updated successfully.");
        setShowUpdateModal(false);
      } else {
        alert("Update failed.");
      }
    } catch (err) {
      console.error("Update error:", err);
    }
  };

  const handleAddChange = (e) => {
    const { name, value } = e.target;
    setNewStudent(prev => ({
      ...prev,
      [name]: name === 'age' ? parseInt(value) : value,
    }));
  };

  const handleAddSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${apiAdd}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newStudent),
      });
      if (res.ok) {
        const saved = await res.json();
        setStudents(prev => [...prev, saved]);
        alert("Student added.");
        setShowAddModal(false);
        setNewStudent({ name: '', age: '', course: '' });
      } else {
        alert("Add failed.");
      }
    } catch (err) {
      console.error("Add error:", err);
    }
  };

  // Pagination logic
  const indexOfLastStudent = currentPage * studentsPerPage;
  const indexOfFirstStudent = indexOfLastStudent - studentsPerPage;
  const currentStudents = students.slice(indexOfFirstStudent, indexOfLastStudent);
  const totalPages = Math.ceil(students.length / studentsPerPage);

  const handlePrevious = () => {
    if (currentPage > 1) setCurrentPage(prev => prev - 1);
  };

  const handleNext = () => {
    if (currentPage < totalPages) setCurrentPage(prev => prev + 1);
  };

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      minHeight: '100vh',
      padding: '40px 500px',
      backgroundColor: '#f5f5f5'
    }}>
      <div style={{
        maxWidth: '800px',
        width: '100%',
        backgroundColor: '#fff',
        padding: '40px',
        borderRadius: '30px',
        boxShadow: '0 0 10px rgba(0,0,0,0.1)'
      }}>
        <h1 style={{ textAlign: 'center', marginBottom: '10px' }}>📋 Student List</h1>
        <hr style={{ marginBottom: '20px' }} />

        <div style={{ textAlign: 'center', marginBottom: '20px' }}>
          <button
            onClick={() => setShowAddModal(true)}
            style={{
              padding: '10px 20px',
              backgroundColor: '#2196f3',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer'
            }}
          >
            ➕ Add Student
          </button>
        </div>

        {loading ? (
          <p style={{ textAlign: 'center' }}>Loading...</p>
        ) : students.length === 0 ? (
          <p style={{ textAlign: 'center' }}>No students found.</p>
        ) : (
          <>
            <table border="1" cellPadding="10" style={{ width: '100%' }}>
              <thead style={{ backgroundColor: '#f0f0f0' }}>
                <tr>
                  <th>ID</th><th>Name</th><th>Age</th><th>Course</th><th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {currentStudents.map(s => (
                  <tr key={s.id}>
                    <td>{s.id}</td><td>{s.name}</td><td>{s.age}</td><td>{s.course}</td>
                    <td>
                      <button
                        onClick={() => handleUpdate(s.id)}
                        style={{
                          backgroundColor: '#4CAF50',
                          color: '#fff',
                          border: 'none',
                          padding: '5px 10px',
                          marginRight: '8px'
                        }}
                      >
                        Update
                      </button>
                      <button
                        onClick={() => handleDelete(s.id)}
                        style={{
                          backgroundColor: '#f44336',
                          color: '#fff',
                          border: 'none',
                          padding: '5px 10px'
                        }}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Pagination controls */}
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
              <button
                onClick={handlePrevious}
                disabled={currentPage === 1}
                style={{
                  padding: '8px 12px',
                  marginRight: '10px',
                  backgroundColor: '#607d8b',
                  color: 'white',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: currentPage === 1 ? 'not-allowed' : 'pointer'
                }}
              >
                ⬅️ Previous
              </button>

              <span>Page {currentPage} of {totalPages}</span>

              <button
                onClick={handleNext}
                disabled={currentPage === totalPages}
                style={{
                  padding: '8px 12px',
                  marginLeft: '10px',
                  backgroundColor: '#607d8b',
                  color: 'white',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: currentPage === totalPages ? 'not-allowed' : 'pointer'
                }}
              >
                Next ➡️
              </button>
            </div>
          </>
        )}

        {/* Update Modal */}
        {showUpdateModal && selectedStudent && (
          <ModalForm
            title="✏️ Update Student"
            student={selectedStudent}
            onChange={handleUpdateChange}
            onClose={() => setShowUpdateModal(false)}
            onSubmit={handleUpdateSubmit}
          />
        )}

        {/* Add Modal */}
        {showAddModal && (
          <ModalForm
            title="➕ Add New Student"
            student={newStudent}
            onChange={handleAddChange}
            onClose={() => setShowAddModal(false)}
            onSubmit={handleAddSubmit}
          />
        )}
      </div>
    </div>
  );
}

function ModalForm({ title, student, onChange, onSubmit, onClose }) {
  return (
    <div style={{
      position: 'fixed',
      top: 0, left: 0, right: 0, bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000
    }}>
      <div style={{
        backgroundColor: '#fff',
        padding: '30px',
        borderRadius: '10px',
        width: '400px',
        boxShadow: '0 0 10px rgba(0,0,0,0.3)'
      }}>
        <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>{title}</h2>
        <form onSubmit={onSubmit}>
          <label>Name:</label>
          <input
            type="text"
            name="name"
            value={student.name}
            onChange={onChange}
            style={{ width: '100%', marginBottom: '10px' }}
            required
          />
          <label>Age:</label>
          <input
            type="number"
            name="age"
            value={student.age}
            onChange={onChange}
            style={{ width: '100%', marginBottom: '10px' }}
            required
          />
          <label>Course:</label>
          <input
            type="text"
            name="course"
            value={student.course}
            onChange={onChange}
            style={{ width: '100%', marginBottom: '20px' }}
            required
          />
          <div style={{ textAlign: 'center' }}>
            <button type="submit" style={{
              backgroundColor: '#4CAF50',
              color: 'white',
              padding: '8px 15px',
              border: 'none',
              borderRadius: '5px',
              marginRight: '10px'
            }}>💾 Save</button>
            <button type="button" onClick={onClose} style={{
              backgroundColor: '#f44336',
              color: 'white',
              padding: '8px 15px',
              border: 'none',
              borderRadius: '5px'
            }}>❌ Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default StudentList;
